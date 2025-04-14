package com.mehatronics.axle_load.ble.handler;

import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToCalibrationTable;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToConfiguration;
import static com.mehatronics.axle_load.utils.ByteUtils.intToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToFourBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToTwoBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.stringToBytes;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVENTY_SEVEN;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND_DECIMAL;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.READ_CHARACTERISTIC_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.ble.executor.GattCommandExecutor;
import com.mehatronics.axle_load.ble.manager.GattConnectionManager;
import com.mehatronics.axle_load.ble.parser.GattDataParser;
import com.mehatronics.axle_load.command.CommandStateHandler;
import com.mehatronics.axle_load.command.factory.impl.DefaultCommandStateFactory;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BluetoothGattCallbackHandler extends BluetoothGattCallback {
    private final MutableLiveData<DeviceDetails> deviceDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<SensorConfig> sensorConfigLiveData = new MutableLiveData<>();
    private final Queue<BluetoothGattCharacteristic> characteristicsQueue = new LinkedList<>();
    private final List<CalibrationTable> table = new ArrayList<>();
    private final List<byte[]> values = new LinkedList<>();
    private boolean isReadingAllCharacteristics = false;
    private boolean areCharacteristicsReads = false;
    private boolean isConfigurationSaved = false;
    private boolean isConnected = false;
    private final byte[] value = new byte[68];
    private CommandStateHandler commandStateHandler;
    private final GattDataParser gattDataParser;
    private final GattConnectionManager connectionManager;
    private GattCommandExecutor executor;

    public BluetoothGattCallbackHandler() {
        var commandStateFactory = new DefaultCommandStateFactory();
        this.commandStateHandler = commandStateFactory.createInitialState();
        this.gattDataParser = new GattDataParser();
        this.connectionManager = new GattConnectionManager();
        this.executor = new GattCommandExecutor(commandStateHandler, this);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (isStatusOk(newState, BluetoothGatt.STATE_CONNECTED)) {
            connectionManager.onConnected(gatt);
            updateStateAfterConnect();
        } else if (isStatusOk(newState, BluetoothGatt.STATE_DISCONNECTED)) {
            connectionManager.onDisconnected();
            resetStateAfterDisconnect();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            Log.d("MyTag", "Services discovered, starting to read all characteristics...");
            readAllCharacteristics(gatt);
        }
    }

    @Override
    public void onCharacteristicRead(
            BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            values.add(characteristic.getValue());
            if (isReadingAllCharacteristics) {
                readNextCharacteristic(gatt);
            } else {
                if (areCharacteristicsReads) {
                    var bytes = characteristic.getValue();
                    if (isMatchingCommand(bytes, 0, SEVEN_COMMAND)
                            && isMatchingCommand(bytes, 1, FIRST_COMMAND)) {
                        areCharacteristicsReads = false;
                        sensorConfigLiveData.postValue(convertBytesToConfiguration(bytes));
                    }
                    if (isMatchingCommand(bytes, 0, FIRST_COMMAND)) {
                        convertBytesToCalibrationTable(bytes, table);
                    }
                }
                if (isConnected && values.size() >= 9) {
                    deviceDetailsLiveData.postValue(gattDataParser.parseDeviceDetails(values, table));
                }
                writeToCharacteristic(gatt);
            }
        }
    }

    @Override
    public void onCharacteristicWrite(
            BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            if (characteristic.getUuid().equals(WRITE_CHARACTERISTIC_DPS)) {
                BluetoothGattService service = gatt.getService(USER_SERVICE_DPS);
                var readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_DPS);
                if (readCharacteristic != null) {
                    try {
                        gatt.readCharacteristic(readCharacteristic);
                    } catch (SecurityException e) {
                        Log.d("MyTag", "Security exception: " + e.getMessage());
                    }
                }
            }
        }
    }

    public LiveData<Boolean> isConnectedLiveData() {
        return connectionManager.getConnectionStatus();
    }

    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return deviceDetailsLiveData;
    }

    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return sensorConfigLiveData;
    }

    public void clearDetails() {
        deviceDetailsLiveData.setValue(null);
    }

    public void resetState() {
        isConnected = false;
    }

    public void setConfigurationSaved() {
        isConfigurationSaved = true;
    }

    public boolean isConfigurationSaved() {
        return isConfigurationSaved;
    }

    public void writeToCharacteristic(BluetoothGatt gatt) {
        Arrays.fill(value, (byte) 0);

        var service = gatt.getService(USER_SERVICE_DPS);
        var writeCharacteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_DPS);

        determineAndSetCommand(gatt);
        writeCharacteristic.setValue(value);

        try {
            gatt.writeCharacteristic(writeCharacteristic);
        } catch (SecurityException e) {
            Log.d("MyTag", "Security exception: " + e.getMessage());
        }
    }

    public void saveConfiguration() {
        var sensorConfig = sensorConfigLiveData.getValue();
        if (sensorConfig == null) {
            return;
        }

        setConfigureSettings(sensorConfig);

        isConfigurationSaved = false;
    }

    private boolean isMatchingCommand(byte[] bytes, int index, int command) {
        return (bytes[index] & ZERO_COMMAND_BINARY) == command && value[index] == command;
    }

    private boolean isStatusOk(int actual, int expected) {
        return actual == expected;
    }

    private void readAllCharacteristics(BluetoothGatt gatt) {
        characteristicsQueue.clear();
        for (BluetoothGattService service : gatt.getServices()) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                    characteristicsQueue.add(characteristic);
                }
            }
        }
        isReadingAllCharacteristics = true;
        areCharacteristicsReads = true;
        readNextCharacteristic(gatt);
    }

    private void readNextCharacteristic(BluetoothGatt gatt) {
        if (!characteristicsQueue.isEmpty()) {
            var characteristic = characteristicsQueue.poll();
            if (characteristic != null) {
                try {
                    gatt.readCharacteristic(characteristic);
                } catch (SecurityException e) {
                    Log.d("MyTag", "Security exception: " + e.getMessage());
                }
            }
        } else {
            isReadingAllCharacteristics = false;
            Log.d("MyTag", "Finished reading all characteristics, starting periodic write-read...");
            writeToCharacteristic(gatt);
        }
    }

    private void determineAndSetCommand(BluetoothGatt gatt) {
        commandStateHandler.handle(gatt, this);
    }

    public void setCommand(int commandFirst, int commandSecond) {
        value[0] = (byte) commandFirst;
        value[1] = (byte) commandSecond;
        value[2] = ZERO_COMMAND_DECIMAL;
        value[3] = ZERO_COMMAND_DECIMAL;
    }

    public void setCommandState(CommandStateHandler newState) {
        this.commandStateHandler = newState;
    }

    private void updateStateAfterConnect() {
        Log.d("MyTag", "Connected to device");
        values.clear();
        isConnected = true;
    }

    private void resetStateAfterDisconnect() {
        Log.d("MyTag", "Disconnected from device");
        isConnected = false;
        values.clear();
        table.clear();
        Arrays.fill(value, (byte) 0);
    }

    private void setConfigureSettings(SensorConfig sensorConfig) {
        setCommand(SEVENTY_SEVEN, FIRST_COMMAND);

        intToFourBytes(value, sensorConfig.getConfigSystem(), 4);
        intToFourBytes(value, Float.floatToIntBits(sensorConfig.getMultiplier()), 8);
        intToFourBytes(value, Float.floatToIntBits(sensorConfig.getOffset()), 12);

        intToTwoBytes(value, sensorConfig.getBatteryMicrovoltsPerStep(), 16);
        intToTwoBytes(value, sensorConfig.getMessageDeliveryPeriod(), 18);
        intToTwoBytes(value, sensorConfig.getMeasurementPeriod(), 20);

        intToTwoBytes(value, sensorConfig.getDistanceBetweenAxlesOneTwoMm(), 22);
        intToTwoBytes(value, sensorConfig.getDistanceBetweenAxlesTwoThreeMm(), 24);
        intToTwoBytes(value, sensorConfig.getDistanceToWheel(), 26);

        intToBytes(value, sensorConfig.getConfigType(), 28);
        intToBytes(value, sensorConfig.getInstallationPoint(), 29);

        stringToBytes(value, sensorConfig.getStateNumber());
    }
}
