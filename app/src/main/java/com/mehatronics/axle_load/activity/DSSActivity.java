package com.mehatronics.axle_load.activity;

import static com.mehatronics.axle_load.entities.enums.DeviceType.DSS;

import android.os.Bundle;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.DeviceListBinder;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DSSActivity extends BaseBluetoothActivity {
    private DeviceListBinder deviceListBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dss);

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
                    && deviceNavigator.isDeviceDetailsFragmentVisible()) {
                deviceNavigator.showDeviceDetailsFragment();
            }
        });
    }

    public void setupBluetooth() {
        bluetoothViewModel.startScan(DSS);
    }
}
