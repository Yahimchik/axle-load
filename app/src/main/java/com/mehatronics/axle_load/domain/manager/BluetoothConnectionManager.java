package com.mehatronics.axle_load.domain.manager;

import static com.mehatronics.axle_load.utils.constants.ValueConstants.MAX_RECONNECT_ATTEMPTS;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.handler.ConnectionHandler;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

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
    public void connect(Device device) {
        disconnect();
        gattCallbackHandler.resetState();
        Log.d("MyTag", "Connecting to device...");
        BluetoothDevice bluetoothDevice = device.getDevice();
        safeConnectGatt(bluetoothDevice, "Security exception: ");
    }

    @Override
    public void reconnect(BluetoothDevice device) {
        if (reconnectAttempts > MAX_RECONNECT_ATTEMPTS) {
            return;
        }

        reconnectAttempts++;

        Log.d("MyTag", "Reconnecting to device...");
        safeConnectGatt(device, "Reconnect Security exception: ");
    }

    @Override
    public void onConnected() {
        reconnectAttempts = 0;
        Log.d("MyTag", "Reconnect attempts reset to 0");
    }

    @Override
    public void disconnect() {
        if (bluetoothGatt != null) {
            try {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                bluetoothGatt = null;
                Log.d("MyTag", "Disconnected from device");
            } catch (SecurityException e) {
                Log.d("MyTag", "Security exception: " + e.getMessage());
            }
        }
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

    private void safeConnectGatt(BluetoothDevice bluetoothDevice, String msg) {
        try {
            bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallbackHandler);
        } catch (SecurityException e) {
            Log.d("MyTag", msg + e.getMessage());
        }
    }
}
