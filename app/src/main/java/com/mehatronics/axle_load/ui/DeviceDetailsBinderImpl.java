package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;
import static com.mehatronics.axle_load.utils.TextWatcherFactory.createWatcher;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setBatteryLevel;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setDeviceName;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setFirmwareVersion;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setHardWareVersion;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setPressure;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setWeight;
import static com.mehatronics.axle_load.utils.format.SensorConfigFormat.setMeasurementPeriod;
import static com.mehatronics.axle_load.utils.format.SensorConfigFormat.setMessageDeliveryPeriod;
import static com.mehatronics.axle_load.utils.format.SensorConfigFormat.setStateNumber;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.databinding.FragmentDeviceDetailsBinding;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.utils.CalibrationTableManager;

import java.util.ArrayList;
import java.util.List;

public class DeviceDetailsBinderImpl {
    private final FragmentDeviceDetailsBinding binding;
    private final CalibrationTableAdapter tableAdapter;
    private final CalibrationTableManager tableManager;

    public DeviceDetailsBinderImpl(View view, CalibrationTableManager tableManager) {
        this.tableManager = tableManager;
        binding = FragmentDeviceDetailsBinding.bind(view); // Binding the views
        tableAdapter = new CalibrationTableAdapter();
        initRecyclerView(view, R.id.calibrationRecyclerView, tableAdapter);
    }

    @SuppressLint("SetTextI18n")
    public void bind(DeviceDetails deviceDetails) {
        bindDeviceFields(deviceDetails);
        tableManager.createCalibrationTable(tableAdapter, deviceDetails);
    }

    private void bindDeviceFields(DeviceDetails deviceDetails) {
        binding.deviceNameTextView.setText(setDeviceName(deviceDetails));
        binding.firmwareVersionValueTextView.setText(setFirmwareVersion(deviceDetails));
        binding.hardwareVersionValueTextView.setText(setHardWareVersion(deviceDetails));
        binding.batteryLevelValueTextView.setText(setBatteryLevel(deviceDetails));
        binding.weightValueTextView.setText(setWeight(deviceDetails));
        binding.pressureValueTextView.setText(setPressure(deviceDetails));
    }

    @SuppressLint("SetTextI18n")
    public void bindConfigure(SensorConfig sensorConfig) {
        setUpInstallationPoint(sensorConfig, binding.installationPointSpinner);
        populateSensorFields(sensorConfig);
        attachSensorListeners(sensorConfig);
    }

    public void setupSaveButton(View.OnClickListener listener) {
        binding.saveConfigurationButton.setOnClickListener(listener);
    }

    private void populateSensorFields(SensorConfig sensorConfig) {
        binding.messageDeliveryPeriodEditText.setText(setMessageDeliveryPeriod(sensorConfig));
        binding.measurementPeriodEditText.setText(setMeasurementPeriod(sensorConfig));
        binding.stateNumber.setText(setStateNumber(sensorConfig));
    }

    private void attachSensorListeners(SensorConfig sensorConfig) {
        binding.messageDeliveryPeriodEditText
                .addTextChangedListener(createWatcher(sensorConfig::setMessageDeliveryPeriod));
        binding.measurementPeriodEditText
                .addTextChangedListener(createWatcher(sensorConfig::setMeasurementPeriod));
        binding.stateNumber
                .addTextChangedListener(createWatcher(sensorConfig::setStateNumber));
    }

    private void setUpInstallationPoint(SensorConfig sensorConfig, Spinner spinner) {
        List<String> pointOptions = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            pointOptions.add(tableManager.getInstallationPointDescription(i));
        }

        ArrayAdapter<String> pointAdapter = new ArrayAdapter<>(binding.getRoot().getContext(), android.R.layout.simple_spinner_item, pointOptions);
        pointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(pointAdapter);

        int installationPoint = sensorConfig.getInstallationPoint();
        String description = tableManager.getInstallationPointDescription(installationPoint);
        int spinnerPosition = pointAdapter.getPosition(description);
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sensorConfig.setInstallationPoint(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
