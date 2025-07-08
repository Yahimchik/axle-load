package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.constants.BundleKeys.AXIS_NUMBER;
import static com.mehatronics.axle_load.constants.BundleKeys.AXIS_SIDE;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.data.service.SensorSelectionService;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.entities.enums.ScreenType;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.domain.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceSelectionCallback;
import com.mehatronics.axle_load.ui.binder.AvailableListBinder;
import com.mehatronics.axle_load.ui.binder.AxisViewBinder;
import com.mehatronics.axle_load.ui.binder.BaseBinder;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

public abstract class BaseSensorFragment extends Fragment implements MessageCallback {
    @Inject
    protected SensorSelectionService manager;
    @Inject
    protected FragmentNavigator navigator;

    protected BluetoothHandlerContract contract;
    protected DeviceViewModel viewModel;
    protected BluetoothHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BluetoothHandlerContract)
            contract = (BluetoothHandlerContract) context;
        if (context instanceof BaseBluetoothActivity)
            handler = ((BaseBluetoothActivity) context).getBluetoothHandler();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers(view);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    protected <T> void observe(LiveData<T> liveData, Observer<T> observer) {
        liveData.observe(getOwner(), observer);
    }

    protected LifecycleOwner getOwner() {
        return getViewLifecycleOwner();
    }

    protected void observeDeviceSelection(OnDeviceSelectionCallback callback) {
        manager.observeSelectedDevice(getParentFragmentManager(), getOwner(), callback);
    }

    protected void setupSnackBarCallback(MessageCallback callback) {
        viewModel.setSnackBarCallback(callback);
    }

    protected int getAxisNumber() {
        return requireArguments().getInt(AXIS_NUMBER);
    }

    protected AxisSide getAxisSide() {
        return AxisSide.valueOf(requireArguments().getString(AXIS_SIDE));
    }

    protected void handleAxisClickEvent(Event<InstalationPoint> event) {
        InstalationPoint data = event.getContentIfNotHandled();
        if (data != null) {
            manager.openSensorSelection(navigator, data, new AvailableSensorFragment());
        }
    }

    protected void onSelected(Device device) {
        viewModel.markMacAsSelected(device);
        manager.returnSelectedDevice(
                getParentFragmentManager(),
                requireActivity(),
                getAxisNumber(),
                getAxisSide(),
                device
        );
    }

    protected abstract BaseBinder createBinder(View view);

    protected abstract ScreenType getScreenType();

    private void setupObservers(View view) {
        observe(viewModel.getMessage(), contract::showMessage);

        switch (getScreenType()) {
            case CONFIGURE -> {
                var binder = (AxisViewBinder) createBinder(view);

                observe(viewModel.getAxisList(), binder::submitList);
                observe(viewModel.getAxisClick(), this::handleAxisClickEvent);
                observe(viewModel.getSavedStateLiveData(), binder::setSavedState);

                observeDeviceSelection(viewModel::setDeviceToAxis);

                viewModel.method(getOwner());
            }
            case AVAILABLE -> {
                var binder = (AvailableListBinder) createBinder(view);
                observe(viewModel.getScannedDevices(), viewModel::updateScannedDevices);
                observe(viewModel.getScannedDevicesLiveData(), binder::updateDevices);
            }
        }
    }
}