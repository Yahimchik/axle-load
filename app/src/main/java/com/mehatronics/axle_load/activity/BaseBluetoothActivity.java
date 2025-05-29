package com.mehatronics.axle_load.activity;

import static android.R.id.content;
import static com.mehatronics.axle_load.R.id.buttonGoToAxes;
import static dagger.hilt.android.EntryPointAccessors.fromActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.LoadingManager;
import com.mehatronics.axle_load.ble.handler.BluetoothHandler;
import com.mehatronics.axle_load.ble.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.di.LoadingManagerEntryPoint;
import com.mehatronics.axle_load.entities.enums.DeviceType;
import com.mehatronics.axle_load.fragment.DeviceDetailsFragment;
import com.mehatronics.axle_load.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.DeviceListBinder;
import com.mehatronics.axle_load.viewModel.DeviceViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseBluetoothActivity extends AppCompatActivity implements BluetoothHandlerContract {
    private DeviceViewModel deviceViewModel;
    private FragmentNavigator fragmentNavigator;
    private BluetoothHandler bluetoothHandler;
    private DeviceListBinder deviceListBinder;
    private LoadingManager loadingManager;
    private boolean isAttemptingToConnect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingManager = fromActivity(this, LoadingManagerEntryPoint.class).getLoadingManager();
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        fragmentNavigator = new FragmentNavigator(this);
        bluetoothHandler = new BluetoothHandler(deviceViewModel, this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initializeInterface();
        setupObservers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceViewModel.clearScannedDevices();
        deviceViewModel.stopScan();
        deviceViewModel.clearDetails();
        deviceViewModel.disconnect();
    }

    @Override
    public void showFragment() {
        fragmentNavigator.showFragment(new DeviceDetailsFragment());
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
    public void showMessage(String message) {
        Snackbar.make(findViewById(content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void initConfigureButton() {
        fragmentNavigator.initConfigureButton(findViewById(buttonGoToAxes));
    }

    public void resetDeviceNavigatorState() {
        if (fragmentNavigator != null) {
            fragmentNavigator.resetState();
        }
    }

    private void initializeInterface() {
        loadingManager.init(findViewById(content));
        deviceListBinder = new DeviceListBinder(findViewById(content), bluetoothHandler::onDeviceSelected);
        initConfigureButton();
    }

    private void setupObservers() {
        deviceViewModel.getScannedDevices().observe(this, deviceListBinder::updateDevices);
        deviceViewModel.getDeviceDetails().observe(this, bluetoothHandler::handleDeviceDetails);
        deviceViewModel.isConnectedLiveData().observe(this, bluetoothHandler::handleConnectionState);
    }

    protected void setupBluetooth(DeviceType deviceType) {
        deviceViewModel.startScan(deviceType);
    }
}