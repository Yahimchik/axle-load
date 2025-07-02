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
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.binder.AvailableListBinder;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.viewModel.ConfigureViewModel;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AvailableSensorFragment extends Fragment implements MessageCallback {
    @Inject
    protected DeviceMapper mapper;
    private DeviceViewModel viewModel;
    private ConfigureViewModel configureViewModel;
    private AvailableListBinder binder;
    private View view;
    private int axisNumber;
    private AxisSide axisSide;

    public static AvailableSensorFragment newInstance(int axisNumber, AxisSide axisSide) {
        AvailableSensorFragment fragment = new AvailableSensorFragment();
        Bundle args = new Bundle();
        args.putInt("axisNumber", axisNumber);
        args.putString("axisSide", axisSide.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        configureViewModel = new ViewModelProvider(requireActivity()).get(ConfigureViewModel.class);
        configureViewModel.setSnackBarCallback(this);

        if (getArguments() != null) {
            axisNumber = getArguments().getInt("axisNumber");
            axisSide = AxisSide.valueOf(getArguments().getString("axisSide"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_available_sensor, container, false);

        binder = new AvailableListBinder(view, mapper, this::onSensorSelected);
        viewModel.getScannedDevices().observe(getViewLifecycleOwner(), configureViewModel::updateScannedDevices);

        configureViewModel.getScannedDevicesLiveData().observe(getViewLifecycleOwner(), binder::updateDevices);

        return view;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void onSensorSelected(Device device) {
        if (device.isSelected()) return;
        device.setSelected(true);
        configureViewModel.markMacAsSelected(device);
        sendDeviceBack(device);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    private void sendDeviceBack(Device device) {
        Bundle result = new Bundle();
        result.putString("mac", device.getDevice().getAddress()); // или сериализуй всё устройство, если нужно больше полей
        result.putInt("axisNumber", axisNumber);
        result.putString("axisSide", axisSide.name());
        getParentFragmentManager().setFragmentResult("selected_device_result", result);
    }

}