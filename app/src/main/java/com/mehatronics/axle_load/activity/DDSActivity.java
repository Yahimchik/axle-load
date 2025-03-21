package com.mehatronics.axle_load.activity;

import static com.mehatronics.axle_load.entities.enums.DeviceType.DDS;

import android.os.Bundle;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.DeviceListBinder;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DDSActivity extends BaseBluetoothActivity {
    private DeviceListBinder deviceListBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dds);

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
            if (deviceDetails != null
                    && bluetoothViewModel.isConnected()
                    && deviceNavigator.isFragmentNotVisible()) {
                deviceNavigator.showDeviceDetailsFragment();
            }
        });
    }

    public void setupBluetooth() {
        bluetoothViewModel.startScan(DDS);
    }
}

