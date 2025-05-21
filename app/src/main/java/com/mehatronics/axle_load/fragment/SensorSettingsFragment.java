package com.mehatronics.axle_load.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.adapter.sensor.SensorAdapter;
import com.mehatronics.axle_load.databinding.FragmentSensorSettingsBinding;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.notification.SnackBarCallback;
import com.mehatronics.axle_load.viewModel.DeviceViewModel;
import com.mehatronics.axle_load.viewModel.SensorViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SensorSettingsFragment extends Fragment implements SnackBarCallback {
    private FragmentSensorSettingsBinding binding;
    private DeviceViewModel deviceViewModel;
    private SensorViewModel sensorViewModel;
    private SensorAdapter sensorAdapter;
    private String sensorPosition;
    private int axisIndex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sensorPosition = getArguments().getString("sensorPosition");
            axisIndex = getArguments().getInt("axisIndex", 0);
        }
        sensorViewModel = new ViewModelProvider(requireActivity()).get(SensorViewModel.class);
        sensorViewModel.setSnackBarCallback(this);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSensorSettingsBinding.inflate(inflater, container, false);

        binding.sensorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sensorAdapter = new SensorAdapter(this::onSensorSelected);
        binding.sensorRecyclerView.setAdapter(sensorAdapter);

        deviceViewModel.getScannedDevices().observe(getViewLifecycleOwner(), devices
                -> sensorViewModel.updateScannedDevices(devices));

        sensorViewModel.getScannedDevicesLiveData().observe(getViewLifecycleOwner(), processedDevices
                -> sensorAdapter.submitList(processedDevices));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSensorSelected(Device device) {
        if (device.isSelected()) return;

        device.setSelected(true);
        sensorViewModel.markMacAsSelected(device.getDevice().getAddress());
        sensorViewModel.setSensorImage(axisIndex, sensorPosition, "axle_sensor_" + sensorPosition);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
}
