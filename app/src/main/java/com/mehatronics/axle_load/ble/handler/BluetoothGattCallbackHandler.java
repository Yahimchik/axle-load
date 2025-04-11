package com.mehatronics.axle_load.ble.handler;

import static com.mehatronics.axle_load.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToCalibrationTable;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToConfiguration;
import static com.mehatronics.axle_load.utils.ByteUtils.stringToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToFourBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToTwoBytes;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToBattery;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToDate;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToString;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToValue;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SECOND_COMMAND;
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

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.entities.enums.CommandState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BluetoothGattCallbackHandler extends BluetoothGattCallback {
    private final MutableLiveData<Boolean> isConnectedLiveData = new MutableLiveData<>(false);
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
    private CommandState commandState = CommandState.FIRST;

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (isStatusOk(newState, BluetoothGatt.STATE_CONNECTED)) {
            try {
                gatt.discoverServices();
                updateStateAfterConnect();
            } catch (SecurityException e) {
                Log.e("MyTag", "SecurityException: " + e.getMessage());
            }
        } else if (isStatusOk(newState, BluetoothGatt.STATE_DISCONNECTED)) {
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
                        SensorConfig sensorConfig = convertBytesToConfiguration(bytes);
                        Log.d("MyTag", sensorConfig.toString());

                        sensorConfigLiveData.postValue(sensorConfig);
                    }
                    if (isMatchingCommand(bytes, 0, FIRST_COMMAND)) {
                        convertBytesToCalibrationTable(bytes, table);
                    }
                }
                if (isConnected && values.size() >= 9) {
                    deviceDetailsLiveData.postValue(createDeviceDetails(values.size() - 1));
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
        return isConnectedLiveData;
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

    public void save() {
        isConfigurationSaved = true;
    }

    // При попытке повторно сохранить значение
    // configType может установить значение
    // равное 8, необходимо это исправить
    public void saveConfiguration(BluetoothGatt gatt) {
        if (Boolean.FALSE.equals(isConnectedLiveData.getValue())) {
            try {
                gatt.connect();
                Log.d("MyTag", "Connecting");
            } catch (SecurityException e) {
                //
            }
        }
        isConfigurationSaved = true;

        SensorConfig sensorConfig = sensorConfigLiveData.getValue();
        if (sensorConfig == null) {
            return;
        }

        setConfigureSettings(sensorConfig);

        sensorConfigLiveData.postValue(sensorConfig);
        isConfigurationSaved = false;
    }

    public void writeToCharacteristic(BluetoothGatt gatt) {
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
        switch (commandState) {
            case FIRST:
                setCommand(FIRST_COMMAND, ZERO_COMMAND_DECIMAL);
                commandState = CommandState.SECOND;
                Log.d("MyTag", "First command sent");
                break;

            case SECOND:
                setCommand(SEVEN_COMMAND, FIRST_COMMAND);
                commandState = isConfigurationSaved ? CommandState.CONFIGURE : CommandState.FINAL;
                Log.d("MyTag","Second command sent");
                break;

            case CONFIGURE:
                saveConfiguration(gatt);
                Log.d("MyTag", "Configuration sent");
                break;

            case FINAL:
                setCommand(SEVEN_COMMAND, SECOND_COMMAND);
                break;
        }
    }

    private void setCommand(int commandFirst, int commandSecond) {
        value[0] = (byte) commandFirst;
        value[1] = (byte) commandSecond;
        value[2] = ZERO_COMMAND_DECIMAL;
        value[3] = ZERO_COMMAND_DECIMAL;
    }

    private DeviceDetails createDeviceDetails(int size) {
        if (values.size() < 8) {
            return null;
        }

        String deviceName = convertBytesToString(values.get(2));
        String dateManufacture = convertBytesToDate(values.get(3));
        String manufacturer = convertBytesToString(values.get(4));
        String modelType = convertBytesToString(values.get(5));
        String serialNumber = convertBytesToString(values.get(6));
        String firmwareVersion = convertBytesToString(values.get(7));
        String hardwareVersion = convertBytesToString(values.get(8));
        String batteryLevel = convertBytesToBattery(values.get(9));
        String weight = convertBytesToValue(values.get(size), WEIGHT);
        String pressure = convertBytesToValue(values.get(size), PRESSURE);

        return new DeviceDetails.Builder()
                .setDeviceName(deviceName)
                .setDateManufacturer(dateManufacture)
                .setManufacturer(manufacturer)
                .setModelType(modelType)
                .setSerialNumber(serialNumber)
                .setFirmwareVersion(firmwareVersion)
                .setHardWareVersion(hardwareVersion)
                .setBatteryLevel(batteryLevel)
                .setWeight(weight)
                .setPressure(pressure)
                .setTable(table)
                .build();
    }

    private void updateStateAfterConnect() {
        Log.d("MyTag", "Connected to device");
        values.clear();
        isConnected = true;
        isConnectedLiveData.postValue(true);
    }

    private void resetStateAfterDisconnect() {
        Log.d("MyTag", "Disconnected from device");
        isConnected = false;
        values.clear();
        table.clear();
        isConnectedLiveData.postValue(false);
        Arrays.fill(value, (byte) 0);
    }

    private void setConfigureSettings(SensorConfig sensorConfig) {
        Arrays.fill(value, (byte) 0);

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
        intToBytes(value, sensorConfig.getConfigType(), 29);

        stringToBytes(value, sensorConfig.getStateNumber());
    }
}
