package com.mehatronics.axle_load.ble.handler;

import static com.mehatronics.axle_load.utils.constants.UuidConstants.READ_CHARACTERISTIC_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.ble.manager.GattConnectionManager;
import com.mehatronics.axle_load.ble.processor.GattReadProcessor;
import com.mehatronics.axle_load.ble.processor.GattWriteProcessor;
import com.mehatronics.axle_load.ble.parser.GattDataParser;
import com.mehatronics.axle_load.command.CommandStateHandler;
import com.mehatronics.axle_load.command.factory.impl.DefaultCommandStateFactory;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

public class BluetoothGattCallbackHandler extends BluetoothGattCallback {
    private final GattConnectionManager connectionManager;
    private final GattReadProcessor gattReadProcessor;
    private final GattWriteProcessor writeProcessor;
    private final GattDataParser gattDataParser;
    private CommandStateHandler stateHandler;

    public BluetoothGattCallbackHandler() {
        var commandStateFactory = new DefaultCommandStateFactory();
        this.gattDataParser = new GattDataParser();
        this.stateHandler = commandStateFactory.createInitialState();
        this.connectionManager = new GattConnectionManager();
        this.writeProcessor = new GattWriteProcessor();
        this.gattReadProcessor = new GattReadProcessor();
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
            gattReadProcessor.readAllCharacteristics(gatt);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            gattReadProcessor.handleRead(gatt, characteristic);
            if (!gattReadProcessor.isReadingAll()) {
                writeToCharacteristic(gatt);
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            if (characteristic.getUuid().equals(WRITE_CHARACTERISTIC_DPS)) {
                gattReadProcessor.readNextAfterWrite(gatt);
            }
        }
    }

    public LiveData<Boolean> isConnectedLiveData() {
        return connectionManager.getConnectionStatus();
    }

    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return gattReadProcessor.getDeviceDetailsLiveData();
    }

    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return gattReadProcessor.getSensorConfigureLiveData();
    }

    public void clearDetails() {
        gattReadProcessor.clearDetails();
    }

    public void resetState() {
        gattReadProcessor.updateState(false);
    }

    public void setConfigurationSaved(boolean value) {
        gattReadProcessor.setConfigurationSaved(value);
    }

    public boolean isConfigurationSaved() {
        return gattReadProcessor.isConfigurationSaved();
    }

    public void writeToCharacteristic(BluetoothGatt gatt) {
        writeProcessor.clearBuffer();
        stateHandler.handle(gatt, this);
        writeProcessor.write(gatt);
    }

    public void saveConfiguration() {
        var sensorConfig = gattReadProcessor.getSensorConfigureLiveData().getValue();
        if (sensorConfig == null) {
            return;
        }
        gattDataParser.setConfigureSettings(sensorConfig, writeProcessor.getBuffer());
        gattReadProcessor.setConfigurationSaved(false);
    }

    private boolean isStatusOk(int actual, int expected) {
        return actual == expected;
    }

    public void setCommand(int commandFirst, int commandSecond) {
        writeProcessor.setCommand(commandFirst, commandSecond);
    }

    public void setCommandState(CommandStateHandler newState) {
        this.stateHandler = newState;
    }

    private void updateStateAfterConnect() {
        gattReadProcessor.updateState(true);
        Log.d("MyTag", "Connected to device");
    }

    private void resetStateAfterDisconnect() {
        gattReadProcessor.updateState(false);
        Log.d("MyTag", "Disconnected from device");
    }
}
