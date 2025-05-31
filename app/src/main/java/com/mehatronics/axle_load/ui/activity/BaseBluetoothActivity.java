package com.mehatronics.axle_load.ui.activity;

import static android.R.id.content;
import static com.mehatronics.axle_load.R.id.buttonGoToAxes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.LoadingManager;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.domain.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.ui.fragment.DeviceDetailsFragment;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.binder.DeviceListBinder;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseBluetoothActivity extends AppCompatActivity implements BluetoothHandlerContract {
    private DeviceViewModel deviceViewModel;
    @Inject
    protected FragmentNavigator fragmentNavigator;
    @Inject
    protected ResourceProvider resourceProvider;
    private BluetoothHandler bluetoothHandler;
    private DeviceListBinder deviceListBinder;
    private LoadingManager loadingManager;
    @Inject
    protected DeviceMapper deviceMapper;
    private boolean isAttemptingToConnect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        bluetoothHandler = new BluetoothHandler(deviceViewModel, this, resourceProvider);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        loadingManager = new LoadingManager(findViewById(content));
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

    public void onDeviceDetailsFragmentClosed() {
        bluetoothHandler.onDeviceDetailsFragmentClosed();
    }

    public void onDeviceDetailsFragmentOpen() {
        bluetoothHandler.onDeviceDetailsFragmentOpen();
    }

    @Override
    public void showFragment() {
        fragmentNavigator.showFragment(new DeviceDetailsFragment());
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase));
    }

    public void resetDeviceNavigatorState() {
        if (fragmentNavigator != null) {
            fragmentNavigator.resetState();
        }
    }

    private void initializeInterface() {
        deviceListBinder = new DeviceListBinder(
                findViewById(content),
                bluetoothHandler::onDeviceSelected,
                deviceMapper
        );
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