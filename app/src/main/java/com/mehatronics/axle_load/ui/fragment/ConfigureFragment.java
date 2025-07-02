package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.valueOf;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.domain.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.AxisViewBinder;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.viewModel.ConfigureViewModel;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfigureFragment extends Fragment implements MessageCallback, BluetoothHandlerContract {
    @Inject
    protected FragmentNavigator navigator;
    @Inject
    protected ResourceProvider provider;
    private ConfigureViewModel configModel;
    private DeviceViewModel deviceModel;
    private View root;
    private BluetoothHandler handler;
    private AxisViewBinder viewBinder;

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_configure, container, false);

        setUpViewModels();
        setupObservers();
        getValuesFromParentFragment();

        return root;
    }

    @Override
    public void showFragment() {
    }

    @Override
    public void loadingManagerShowLoading(boolean isLoading) {
        viewBinder.showLoading(isLoading);
    }

    @Override
    public void setIsAttemptingToConnect(boolean isAttempting) {
    }

    @Override
    public boolean isAttemptingToConnect() {
        return false;
    }

    @Override
    public void initConfigureButton() {
    }

    @Override
    public void onFragmentClosed() {
    }

    @Override
    public void onFragmentOpen() {
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving() || requireActivity().isFinishing()) {
            configModel.resetSelectedDevices();
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void setUpViewModels() {
        configModel = new ViewModelProvider(this).get(ConfigureViewModel.class);
        deviceModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        handler = new BluetoothHandler.builder()
                .withModel(deviceModel)
                .withModel(configModel)
                .withContract(this)
                .withResource(provider)
                .build();

        viewBinder = new AxisViewBinder.builder()
                .withRoot(root)
                .onClick(handler::onClick)
                .onReset(handler::onReset)
                .onConnect(handler::onConnect)
                .onConfigureClicked(configModel::onConfigureClicked)
                .build();

        configModel.method(getViewLifecycleOwner());
    }

    private void getValuesFromParentFragment() {
        getParentFragmentManager().setFragmentResultListener("selected_device_result", this, (key, bundle) -> {
            String mac = bundle.getString("mac");
            String sideStr = bundle.getString("axisSide");
            int axisNumber = bundle.getInt("axisNumber");
            configModel.setDeviceToAxis(axisNumber, valueOf(sideStr), mac);
        });
    }

    private void setupObservers() {
        configModel.getAxisList().observe(getViewLifecycleOwner(), viewBinder::submitList);
        configModel.getMessage().observe(getViewLifecycleOwner(), this::showMessage);
        configModel.getAxisClick().observe(getViewLifecycleOwner(), this::handleAxisClickEvent);


        deviceModel.getDeviceDetails().observe(getViewLifecycleOwner(), handler::handleDeviceDetails);
        deviceModel.isConnectedLiveData().observe(getViewLifecycleOwner(), handler::handleConnectionState);
    }

    private void handleAxisClickEvent(Event<InstalationPoint> event) {
        InstalationPoint data = event.getContentIfNotHandled();
        if (data != null) {
            navigator.showFragment(AvailableSensorFragment.newInstance(data.getAxleNumber(), data.getPosition()));
        }
    }
}
