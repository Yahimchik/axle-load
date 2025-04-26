package com.mehatronics.axle_load.ble.manager;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class GattConnectionManager {
    private final MutableLiveData<Boolean> isConnectedLiveData = new MutableLiveData<>(false);

    @Inject
    public GattConnectionManager() {
    }

    public LiveData<Boolean> getConnectionStatus() {
        return isConnectedLiveData;
    }

    public void onConnected(BluetoothGatt gatt) {
        try {
            gatt.discoverServices();
            isConnectedLiveData.postValue(true);
        } catch (SecurityException e) {
            Log.e("MyTag", "SecurityException: " + e.getMessage());
        }
    }

    public void onDisconnected() {
        isConnectedLiveData.postValue(false);
    }
}
