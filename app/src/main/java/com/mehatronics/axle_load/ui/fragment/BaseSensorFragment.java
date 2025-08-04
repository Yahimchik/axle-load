package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.R.string.selected;
import static com.mehatronics.axle_load.constants.BundleKeys.AXIS_NUMBER;
import static com.mehatronics.axle_load.constants.BundleKeys.AXIS_SIDE;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.data.service.SensorSelectionService;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceSelectionCallback;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.notification.SnackbarManager;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

public abstract class BaseSensorFragment extends Fragment implements MessageCallback {
    @Inject
    protected SensorSelectionService service;
    @Inject
    protected FragmentNavigator navigator;
    @Inject
    protected SnackbarManager manager;

    protected DeviceViewModel viewModel;
    protected BluetoothHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseBluetoothActivity)
            handler = ((BaseBluetoothActivity) context).getBluetoothHandler();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createBinder(view);
        observe(viewModel.getMessage(), this::showMessage);
    }

    @Override
    public void showMessage(String message) {
        manager.showMessage(requireView(), message);
    }

    protected <T> void observe(LiveData<T> liveData, Observer<T> observer) {
        liveData.observe(getOwner(), observer);
    }

    protected void observeDeviceSelection(OnDeviceSelectionCallback callback) {
        service.observeSelectedDevice(getParentFragmentManager(), getOwner(), callback);
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
            service.openSensorSelection(navigator, data, new AvailableSensorFragment());
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    protected void onSelected(Device device) {
        viewModel.markMacAsSelected(device);
        showMessage(getString(selected, device.getDevice().getName()));
        service.returnSelectedDevice(
                getParentFragmentManager(),
                requireActivity(),
                getAxisNumber(),
                getAxisSide(),
                device
        );
    }

    protected abstract void createBinder(View view);

    private LifecycleOwner getOwner() {
        return getViewLifecycleOwner();
    }
}