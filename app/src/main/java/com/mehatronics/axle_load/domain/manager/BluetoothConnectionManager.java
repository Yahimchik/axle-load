package com.mehatronics.axle_load.domain.manager;

import static com.mehatronics.axle_load.constants.ValueConstants.MAX_RECONNECT_ATTEMPTS;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;
import com.mehatronics.axle_load.helper.SingleLiveEvent;
import com.mehatronics.axle_load.ui.adapter.listener.GattReadListener;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.handler.ConnectionHandler;
import com.mehatronics.axle_load.ui.adapter.listener.PasswordDialogListener;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class BluetoothConnectionManager implements ConnectionHandler {
    @Inject
    protected BluetoothGattCallbackHandler gattCallbackHandler;
    private BluetoothGatt bluetoothGatt;
    private final Context context;
    private int reconnectAttempts = 0;

    @Inject
    public BluetoothConnectionManager(@ApplicationContext Context context) {
        this.context = context;
    }

    @Inject
    public void initAfterConstructor() {
        gattCallbackHandler.setReconnectDelegate(this);
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void connect(Device device) {
        gattCallbackHandler.resetState();
        Log.d("MyTag", "Connecting to device...");
        safeConnectGatt(device.getDevice());
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void reconnect(BluetoothDevice device) {
        if (reconnectAttempts > MAX_RECONNECT_ATTEMPTS) {
            return;
        }

        reconnectAttempts++;

        Log.d("MyTag", "Reconnecting to device...");
        safeConnectGatt(device);
    }

    @Override
    public void onConnected() {
        reconnectAttempts = 0;
        Log.d("MyTag", "Reconnect attempts reset to 0");
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            Log.d("MyTag", "Disconnected from device");
        }
    }

    public void setPasswordListener(PasswordDialogListener listener) {
        gattCallbackHandler.setPasswordDialogListener(listener);
    }

    public void clearPasswordDialogShown() {
        gattCallbackHandler.clearPasswordDialogShown();
    }

    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return gattCallbackHandler.getDeviceDetailsLiveData();
    }

    public void setDeviceDetailsLiveData(DeviceDetails details) {
        gattCallbackHandler.setDeviceDetailsLiveData(details);
    }

    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return gattCallbackHandler.getSensorConfigureLiveData();
    }

    public void clearDetails() {
        gattCallbackHandler.clearDetails();
    }

    public void saveConfiguration() {
        gattCallbackHandler.setConfigurationSaved(true);
        gattCallbackHandler.writeToCharacteristic(bluetoothGatt);
    }

    public void saveTable() {
        gattCallbackHandler.setTableSaved(true);
        gattCallbackHandler.writeToCharacteristic(bluetoothGatt);
    }

    public void rereadCalibrationTable() {
        gattCallbackHandler.rereadCalibrationTable();
    }

    public LiveData<Boolean> isConnectedLiveData() {
        return gattCallbackHandler.isConnectedLiveData();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void safeConnectGatt(BluetoothDevice bluetoothDevice) {
        bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallbackHandler);
    }

    public void setConfigurationSavedLive(boolean value) {
        gattCallbackHandler.setConfigurationSavedLive(value);
    }

    public LiveData<Boolean> getConfigurationSavedLiveData() {
        return gattCallbackHandler.getConfigurationSavedLiveData();
    }

    public void resetPassword(boolean value) {
        gattCallbackHandler.resetPassword(value);
    }

    public void setPassword(boolean value) {
        gattCallbackHandler.setPassword(value);
    }

    public void setListener(GattReadListener listener) {
        gattCallbackHandler.setListener(listener);
    }

    public boolean isSavedToBTCOMMini() {
        return gattCallbackHandler.isSavedToBTCOMMini();
    }

    public void saveToBTCOMMini(){
        gattCallbackHandler.setSavedToBTCOMMini(true);
        gattCallbackHandler.writeToCharacteristic(bluetoothGatt);
    }

    public void setSaveToMiniLive(boolean value) {
        gattCallbackHandler.setSaveToMiniLive(value);
    }

    public SingleLiveEvent<Boolean> getSaveToMiniLive() {
        return gattCallbackHandler.getSaveToMiniLive();
    }
}
