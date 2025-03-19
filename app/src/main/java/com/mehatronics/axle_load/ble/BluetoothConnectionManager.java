package com.mehatronics.axle_load.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.ble.handler.ConnectionStateListener;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class BluetoothConnectionManager {
    private BluetoothGatt bluetoothGatt;
    private final BluetoothGattCallbackHandler gattCallbackHandler;
    private final MutableLiveData<Boolean> connectionStatus = new MutableLiveData<>();
    private final Context applicationContext;

    @Inject
    public BluetoothConnectionManager(@ApplicationContext Context context) {
        this.applicationContext = context;
        gattCallbackHandler = new BluetoothGattCallbackHandler(new ConnectionStateListener() {
            @Override
            public void onConnected() {
                connectionStatus.postValue(true);
            }

            @Override
            public void onDisconnected() {
                connectionStatus.postValue(false);
            }
        });
    }

    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return gattCallbackHandler.getDeviceDetailsLiveData();
    }

    public void clearDetails(){
        gattCallbackHandler.clearDetails();
    }

    public void connectToDevice(Device device) {
        disconnect();
        gattCallbackHandler.resetState();
        Log.d("MyTag", "Connecting to device...");
        BluetoothDevice bluetoothDevice = device.getDevice();
        try {
            bluetoothGatt = bluetoothDevice.connectGatt(
                    applicationContext, false, gattCallbackHandler
            );
        } catch (SecurityException e) {
            Log.e("MyTag", "SecurityException: " + e.getMessage());
        }
    }

    public boolean isConnected(){
        return gattCallbackHandler.isConnected();
    }

    public void disconnect() {
        if (bluetoothGatt != null) {
            try {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                bluetoothGatt = null;
                Log.d("MyTag", "Disconnected from device");
            } catch (SecurityException e) {
                // handle exception
            }
        }
    }
}
