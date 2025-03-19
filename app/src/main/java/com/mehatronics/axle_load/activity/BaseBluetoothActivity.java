package com.mehatronics.axle_load.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.viewModel.BluetoothViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseBluetoothActivity extends AppCompatActivity {
    protected BluetoothViewModel bluetoothViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothViewModel.clearScannedDevices();
        bluetoothViewModel.disconnect();
        bluetoothViewModel.stopScan();
        bluetoothViewModel.clearDetails();
    }
}