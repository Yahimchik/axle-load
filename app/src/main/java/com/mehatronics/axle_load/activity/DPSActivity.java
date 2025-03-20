package com.mehatronics.axle_load.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.enums.DeviceType;
import com.mehatronics.axle_load.fragment.DeviceDetailsFragment;
import com.mehatronics.axle_load.navigation.DeviceNavigator;
import com.mehatronics.axle_load.ui.DeviceListBinder;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DPSActivity extends BaseBluetoothActivity {
    private DeviceListBinder deviceListBinder;
    private DeviceNavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dps);
        navigator = new DeviceNavigator(this);

        initUI();
        setupObservers();
        setupBluetooth();
    }

    private void initUI() {
        deviceListBinder = new DeviceListBinder(
                findViewById(android.R.id.content), bluetoothViewModel::connectToDevice
        );
    }

    private void setupObservers() {
        bluetoothViewModel.getScannedDevices().observe(this, deviceListBinder::updateDevices);
        bluetoothViewModel.getDeviceDetails().observe(this, deviceDetails -> {
            if (deviceDetails != null && bluetoothViewModel.isConnected()) {
                if (!isDeviceDetailsFragmentVisible() && bluetoothViewModel.getDeviceDetails() != null) {
                    navigator.showDeviceDetailsFragment();
                }
            }
        });
    }

    private boolean isDeviceDetailsFragmentVisible() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        return fragment instanceof DeviceDetailsFragment;
    }

    public void setupBluetooth() {
        bluetoothViewModel.startScan(DeviceType.DPS);
    }
}

