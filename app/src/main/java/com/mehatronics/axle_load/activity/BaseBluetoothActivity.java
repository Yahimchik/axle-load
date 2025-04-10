package com.mehatronics.axle_load.activity;

import static android.R.id.content;
import static com.mehatronics.axle_load.R.id.configureButton;
import static dagger.hilt.android.EntryPointAccessors.fromActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.adapter.LoadingManager;
import com.mehatronics.axle_load.ble.handler.BluetoothHandler;
import com.mehatronics.axle_load.ble.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.di.LoadingManagerEntryPoint;
import com.mehatronics.axle_load.entities.enums.DeviceType;
import com.mehatronics.axle_load.navigation.FragmentNavigator;
import com.mehatronics.axle_load.notification.NotificationManager;
import com.mehatronics.axle_load.ui.DeviceListBinder;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseBluetoothActivity extends AppCompatActivity implements BluetoothHandlerContract {
    private NotificationManager notificationManager;
    private BluetoothViewModel bluetoothViewModel;
    private FragmentNavigator fragmentNavigator;
    private BluetoothHandler bluetoothHandler;
    private DeviceListBinder deviceListBinder;
    private LoadingManager loadingManager;
    private boolean isAttemptingToConnect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingManager = fromActivity(this, LoadingManagerEntryPoint.class).getLoadingManager();
        bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);
        fragmentNavigator = new FragmentNavigator(this);
        bluetoothHandler = new BluetoothHandler(bluetoothViewModel, this);
        notificationManager = new NotificationManager(this);
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

    @Override
    public void showFragment() {
        fragmentNavigator.showFragment();
    }

    @Override
    public boolean isFragmentNotVisible() {
        return fragmentNavigator.isFragmentNotVisible();
    }

    @Override
    public void loadingManagerShowLoading(boolean isLoading) {
        loadingManager.showLoading(isLoading);
    }

    @Override
    public void setIsAttemptingToConnect(boolean isAttempting) {
        isAttemptingToConnect = isAttempting;
    }

    @Override
    public boolean isAttemptingToConnect() {
        return isAttemptingToConnect;
    }

    @Override
    public void showSnackBar(String message) {
        notificationManager.showSnackBar(message);
    }

    @Override
    public void initConfigureButton() {
        fragmentNavigator.initConfigureButton(findViewById(configureButton));
    }

    public void resetDeviceNavigatorState() {
        if (fragmentNavigator != null) {
            fragmentNavigator.resetState();
        }
    }

    protected void initUI() {
        loadingManager.init(findViewById(content));
        deviceListBinder = new DeviceListBinder(findViewById(content), bluetoothHandler::onDeviceSelected);
        initConfigureButton();
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