package com.mehatronics.axle_load.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.notification.MessageCallback;
import com.mehatronics.axle_load.ui.DeviceDetailsBinder;
import com.mehatronics.axle_load.viewModel.DeviceViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment implements MessageCallback {
    private DeviceViewModel deviceViewModel;
    private DeviceDetailsBinder detailsBinder;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        deviceViewModel.setSnackBarCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_device_details, container, false);
        detailsBinder = new DeviceDetailsBinder(view, deviceViewModel);

        observeView();
        setupSaveButton();
        setupResetTableBtn();
        setupSaveTableButton();

        return view;
    }

    private void observeView() {
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

    private void setupSaveTableButton() {
        detailsBinder.setupSaveTableButton(v -> {
            int error = deviceViewModel.saveTable();
            var details = deviceViewModel.getDeviceDetails().getValue();
            details.setTable(deviceViewModel.getCalibrationTable().getValue());
            deviceViewModel.setDeviceDetailsLiveData(details);
            Log.d("MyTag", String.valueOf(details));
            deviceViewModel.saveTableToSensor();
            if (error > 0) showMessage(getString(R.string.invalid_detector, error));
        });
    }

    private void setupSaveButton() {
        detailsBinder.setupSaveButton(v -> {
            SensorConfig config = deviceViewModel.getSensorConfigure().getValue();
            if (config != null) {
                detailsBinder.updateSensorConfig(config);
                deviceViewModel.saveSensorConfiguration();
                showMessage(getString(R.string.save_configuration));
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

    @Override
    public void showMessage(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}