package com.mehatronics.axle_load.activity;

import static android.R.id.content;
import static dagger.hilt.android.EntryPointAccessors.fromActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.adapter.LoadingManager;
import com.mehatronics.axle_load.ble.handler.BluetoothHandler;
import com.mehatronics.axle_load.di.LoadingManagerEntryPoint;
import com.mehatronics.axle_load.entities.enums.DeviceType;
import com.mehatronics.axle_load.navigation.DeviceNavigator;
import com.mehatronics.axle_load.ui.DeviceListBinder;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseBluetoothActivity extends AppCompatActivity {
    private BluetoothViewModel bluetoothViewModel;
    private DeviceListBinder deviceListBinder;
    private BluetoothHandler bluetoothHandler;
    public DeviceNavigator deviceNavigator;
    public LoadingManager loadingManager;
    public boolean isAttemptingToConnect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingManager = fromActivity(this, LoadingManagerEntryPoint.class).getLoadingManager();
        bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);
        deviceNavigator = new DeviceNavigator(this);
        bluetoothHandler = new BluetoothHandler(bluetoothViewModel, this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initUI();
        setupObservers();
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

    protected void initUI() {
        loadingManager.init(findViewById(content));
        deviceListBinder = new DeviceListBinder(findViewById(content), bluetoothHandler::onDeviceSelected);
    }

    protected void setupObservers() {
        bluetoothViewModel.getScannedDevices().observe(this, deviceListBinder::updateDevices);
        bluetoothViewModel.getDeviceDetails().observe(this, bluetoothHandler::handleDeviceDetails);
        bluetoothViewModel.isConnectedLiveData().observe(this, bluetoothHandler::handleConnectionState);
    }

    protected void setupBluetooth(DeviceType deviceType) {
        bluetoothViewModel.startScan(deviceType);
    }
}