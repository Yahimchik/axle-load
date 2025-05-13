package com.mehatronics.axle_load.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.databinding.FragmentDeviceDetailsBinding;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.ui.impl.DeviceDetailsBinderImpl;
import com.mehatronics.axle_load.ui.view_binder.DeviceDetailsViewBinder;
import com.mehatronics.axle_load.ui.view_binder.SensorConfigViewBinder;
import com.mehatronics.axle_load.ui.CalibrationTableManager;
import com.mehatronics.axle_load.format.DeviceDetailsFormatter;
import com.mehatronics.axle_load.format.SensorConfigFormatter;
import com.mehatronics.axle_load.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment {
    @Inject
    protected DeviceDetailsFormatter<DeviceDetails> deviceDetailsFormatter;
    @Inject
    protected SensorConfigFormatter<SensorConfig> sensorConfigFormatter;
    @Inject
    protected CalibrationTableManager tableManager;
    @Inject
    protected CalibrationTableAdapter tableAdapter;
    private DeviceDetailsBinderImpl detailsBinder;
    private DeviceViewModel deviceViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        var binding = FragmentDeviceDetailsBinding.inflate(inflater, container, false);
        var detailsView = new DeviceDetailsViewBinder(binding, deviceDetailsFormatter);
        var configView = new SensorConfigViewBinder(binding, sensorConfigFormatter);

        detailsBinder = new DeviceDetailsBinderImpl(
                binding, tableAdapter, tableManager, detailsView, configView
        );

        deviceViewModel.getDeviceDetails().observe(getViewLifecycleOwner(), detailsBinder::bind);
        deviceViewModel.getSensorConfigure().observe(getViewLifecycleOwner(), detailsBinder::bindConfigure);

        detailsBinder.setupSaveButton(v -> deviceViewModel.saveSensorConfiguration());

        return binding.getRoot();
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
