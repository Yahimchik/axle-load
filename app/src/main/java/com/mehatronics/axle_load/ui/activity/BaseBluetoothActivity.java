package com.mehatronics.axle_load.ui.activity;

import static android.R.id.content;
import static com.mehatronics.axle_load.R.id.buttonGoToAxes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.domain.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.LoadingManager;
import com.mehatronics.axle_load.ui.binder.DeviceListBinder;
import com.mehatronics.axle_load.ui.fragment.DeviceDetailsFragment;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseBluetoothActivity extends AppCompatActivity implements BluetoothHandlerContract {
    @Inject
    protected FragmentNavigator navigator;
    @Inject
    protected ResourceProvider provider;
    private DeviceViewModel viewModel;
    private BluetoothHandler handler;
    private DeviceListBinder binder;
    private LoadingManager manager;
    @Inject
    protected DeviceMapper mapper;
    private boolean isAttemptingToConnect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        handler = new BluetoothHandler.builder()
                .withModel(viewModel)
                .withContract(this)
                .withResource(provider)
                .build();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        manager = new LoadingManager(findViewById(content));
        initializeInterface();
        setupObservers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.clearScannedDevices();
        viewModel.stopScan();
        viewModel.clearDetails();
        viewModel.disconnect();
    }

    @Override
    public void onFragmentClosed() {
        handler.onDeviceDetailsFragmentClosed();
    }

    @Override
    public void onFragmentOpen() {
        handler.onDeviceDetailsFragmentOpen();
    }

    @Override
    public void showFragment() {
        navigator.showFragment(new DeviceDetailsFragment());
    }

    @Override
    public void loadingManagerShowLoading(boolean isLoading) {
        manager.showLoading(isLoading);
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
        navigator.initConfigureButton(findViewById(buttonGoToAxes));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase));
    }

    private void initializeInterface() {
        binder = new DeviceListBinder(findViewById(content), handler::onDeviceSelected, mapper);
        initConfigureButton();
    }

    private void setupObservers() {
        viewModel.getScannedDevices().observe(this, binder::updateDevices);
        viewModel.getDeviceDetails().observe(this, handler::handleDeviceDetails);
        viewModel.isConnectedLiveData().observe(this, handler::handleConnectionState);
    }

    protected void setupBluetooth(DeviceType deviceType) {
        viewModel.startScan(deviceType);
    }
}