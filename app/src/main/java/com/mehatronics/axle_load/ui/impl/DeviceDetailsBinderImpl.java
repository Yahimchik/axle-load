package com.mehatronics.axle_load.ui.impl;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.databinding.FragmentDeviceDetailsBinding;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.ui.binder.DeviceDetailsBinder;
import com.mehatronics.axle_load.ui.view_binder.DeviceDetailsViewBinder;
import com.mehatronics.axle_load.ui.view_binder.SensorConfigViewBinder;
import com.mehatronics.axle_load.ui.CalibrationTableManager;

import javax.inject.Inject;

public class DeviceDetailsBinderImpl implements DeviceDetailsBinder {
    private final FragmentDeviceDetailsBinding binding;
    private final CalibrationTableAdapter tableAdapter;
    private final CalibrationTableManager tableManager;
    private final DeviceDetailsViewBinder deviceViewBinder;
    private final SensorConfigViewBinder sensorConfigBinder;

    @Inject
    public DeviceDetailsBinderImpl(FragmentDeviceDetailsBinding binding,
                                   CalibrationTableAdapter adapter,
                                   CalibrationTableManager tableManager,
                                   DeviceDetailsViewBinder deviceViewBinder,
                                   SensorConfigViewBinder sensorConfigBinder) {
        this.binding = binding;
        this.tableAdapter = adapter;
        this.tableManager = tableManager;
        this.deviceViewBinder = deviceViewBinder;
        this.sensorConfigBinder = sensorConfigBinder;

        initRecyclerView(binding.getRoot(), R.id.calibrationRecyclerView, adapter);
    }

    @Override
    public void bind(DeviceDetails deviceDetails) {
        deviceViewBinder.bind(deviceDetails);
        tableManager.createCalibrationTable(tableAdapter, deviceDetails);
    }

    @Override
    public void bindConfigure(SensorConfig sensorConfig) {
        sensorConfigBinder.bind(sensorConfig);
        tableManager.setUpInstallationPoint(sensorConfig, binding.installationPointSpinner);
    }

    @Override
    public void setupSaveButton(View.OnClickListener listener) {
        binding.saveConfigurationButton.setOnClickListener(listener);
    }
}
