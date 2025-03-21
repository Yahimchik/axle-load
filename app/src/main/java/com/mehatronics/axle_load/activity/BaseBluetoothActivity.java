package com.mehatronics.axle_load.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.navigation.DeviceNavigator;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;


import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseBluetoothActivity extends AppCompatActivity {
    protected BluetoothViewModel bluetoothViewModel;
    protected DeviceNavigator deviceNavigator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);
        deviceNavigator = new DeviceNavigator(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothViewModel.clearScannedDevices();
        bluetoothViewModel.stopScan();
        bluetoothViewModel.clearDetails();
        bluetoothViewModel.disconnect();
    }

    public void resetDeviceNavigatorState() {
        if (deviceNavigator != null) {
            deviceNavigator.resetState();
        }
    }
}