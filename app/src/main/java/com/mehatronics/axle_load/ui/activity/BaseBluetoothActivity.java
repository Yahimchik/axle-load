package com.mehatronics.axle_load.ui.activity;

import static android.R.id.content;
import static com.mehatronics.axle_load.R.id.buttonGoToAxes;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;

import android.Manifest;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.domain.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.LoadingManager;
import com.mehatronics.axle_load.ui.binder.DeviceListBinder;
import com.mehatronics.axle_load.ui.fragment.ConfigureFragment;
import com.mehatronics.axle_load.ui.fragment.DeviceDetailsFragment;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.SnackbarManager;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseBluetoothActivity extends AppCompatActivity implements BluetoothHandlerContract {
    @Inject
    protected SnackbarManager snackbarManager;
    @Inject
    protected FragmentNavigator navigator;
    @Inject
    protected ResourceProvider provider;
    @Inject
    protected DeviceMapper mapper;
    @Inject
    protected DeviceTypeRepository repository;

    private DeviceViewModel viewModel;
    private BluetoothHandler handler;
    private DeviceListBinder binder;
    private LoadingManager manager;

    private boolean isAttemptingToConnect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        handler = new BluetoothHandler(viewModel, this, provider, repository);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        manager = new LoadingManager(findViewById(content));
        initializeInterface();
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    protected void onDestroy() {
        super.onDestroy();
        viewModel.clearScannedDevices();
        viewModel.stopScan();
        viewModel.clearDetails();
        viewModel.disconnect();
        repository.setDeviceType(null);
    }

    @Override
    public void showFragment() {
        if (repository.getCurrDeviceType().equals(BT_COM_MINI)) {
            navigator.addHiddenFragment(new DeviceDetailsFragment());
        } else {
            navigator.showFragment(new DeviceDetailsFragment());
        }
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
        snackbarManager.showMessage(findViewById(content), message);
    }

    @Override
    public void initConfigureButton() {
        findViewById(buttonGoToAxes).setOnClickListener(v -> navigator.showFragment(new ConfigureFragment()));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(newBase));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public BluetoothHandler getBluetoothHandler() {
        return handler;
    }

    private void initializeInterface() {
        binder = new DeviceListBinder(findViewById(content), handler::onDeviceSelected, mapper);
        initConfigureButton();
    }

    private void setupObservers() {
        if (repository.getCurrDeviceType().equals(BT_COM_MINI)) {
            viewModel.getBtComMiniDevices().observe(this, binder::updateDevices);
        } else {
            viewModel.getScannedDevices().observe(this, binder::updateDevices);
        }
        viewModel.getDeviceDetails().observe(this, handler::handleDeviceDetails);
        viewModel.isConnectedLiveData().observe(this, handler::handleConnectionState);
    }

    protected void setupBluetooth(DeviceType deviceType) {
        repository.setDeviceType(deviceType);
        viewModel.startScan(deviceType);
        setupObservers();
    }
}