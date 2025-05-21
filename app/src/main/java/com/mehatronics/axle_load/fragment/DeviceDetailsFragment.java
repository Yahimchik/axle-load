package com.mehatronics.axle_load.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.ui.DeviceDetailsBinder;
import com.mehatronics.axle_load.viewModel.DeviceViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment {
    private DeviceViewModel deviceViewModel;
    private DeviceDetailsBinder detailsBinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_details, container, false);
        detailsBinder = new DeviceDetailsBinder(view, deviceViewModel);

        observeViewModel();
        setupSaveButton();
        setupResetTableBtn();

        return view;
    }

    private void observeViewModel() {
        deviceViewModel.getDeviceDetails().observe(getViewLifecycleOwner(), deviceDetails -> {
            detailsBinder.bindInfo(deviceDetails);
            deviceViewModel.updateVirtualPoint(deviceDetails);
        });

        deviceViewModel.getCalibrationTable().observe(getViewLifecycleOwner(), detailsBinder::bindTable);
        deviceViewModel.getSensorConfigure().observe(getViewLifecycleOwner(), detailsBinder::bindConfigure);
    }

    private void setupResetTableBtn() {
        detailsBinder.setupReadFromSensorButton(v -> deviceViewModel.rereadCalibrationTable());
    }

    private void setupSaveButton() {
        detailsBinder.setupSaveButton(v -> {
            SensorConfig config = deviceViewModel.getSensorConfigure().getValue();
            if (config != null) {
                detailsBinder.updateSensorConfig(config);
                deviceViewModel.saveSensorConfiguration();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detailsBinder = null;
        deviceViewModel.clearDetails();
        deviceViewModel.disconnect();
        if (getActivity() instanceof BaseBluetoothActivity) {
            ((BaseBluetoothActivity) getActivity()).resetDeviceNavigatorState();
        }
        Log.d("MyTag", "Device details fragment is closed");
    }
}