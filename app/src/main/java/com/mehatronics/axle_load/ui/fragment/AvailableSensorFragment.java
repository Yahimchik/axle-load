package com.mehatronics.axle_load.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.ui.binder.AvailableListBinder;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;
import com.mehatronics.axle_load.ui.viewModel.SensorViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AvailableSensorFragment extends Fragment implements MessageCallback {
    @Inject
    protected DeviceMapper mapper;
    private DeviceViewModel viewModel;
    private SensorViewModel sensorViewModel;
    private AvailableListBinder binder;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        sensorViewModel = new ViewModelProvider(requireActivity()).get(SensorViewModel.class);
        sensorViewModel.setSnackBarCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_available_sensor, container, false);

        binder = new AvailableListBinder(view, mapper, this::onSensorSelected);
        viewModel.getScannedDevices().observe(getViewLifecycleOwner(), devices
                -> sensorViewModel.updateScannedDevices(devices));

        sensorViewModel.getScannedDevicesLiveData().observe(getViewLifecycleOwner(), processedDevices
                -> binder.updateDevices(processedDevices));
        return view;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void onSensorSelected(Device device) {
        if (device.isSelected()) return;
        device.setSelected(true);
        sensorViewModel.markMacAsSelected(device);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}