package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;
import static com.mehatronics.axle_load.utils.constants.StringConstants.AXLE;
import static com.mehatronics.axle_load.utils.constants.StringConstants.LEFT;
import static com.mehatronics.axle_load.utils.constants.StringConstants.RIGHT;
import static com.mehatronics.axle_load.utils.constants.StringConstants.ZERO;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.databinding.FragmentDeviceDetailsBinding;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DeviceDetailsBinder {
    private final FragmentDeviceDetailsBinding binding;
    private final CalibrationTableAdapter adapter;

    public DeviceDetailsBinder(View view) {
        binding = FragmentDeviceDetailsBinding.bind(view); // Binding the views
        adapter = new CalibrationTableAdapter();
        initRecyclerView(view, R.id.calibrationRecyclerView, adapter);
    }

    @SuppressLint("SetTextI18n")
    public void bind(DeviceDetails deviceDetails) {
        bindDeviceFields(deviceDetails);
        createCalibrationTable(deviceDetails);
    }

    private void createCalibrationTable(DeviceDetails deviceDetails) {
        List<CalibrationTable> originalTable = deviceDetails.getTable();
        List<CalibrationTable> extendedTable = new ArrayList<>(originalTable);

        boolean isTableInitialized = adapter.getItemCount() > 0;
        if (originalTable.size() >= 2) {
            CalibrationTable last = originalTable.get(originalTable.size() - 2);

            float currentPressure = parsePressure(deviceDetails.getPressure());

            int detectorValue = (int) (currentPressure * 10);
            float multiplier = last.getMultiplier();
            CalibrationTable virtualPoint = new CalibrationTable(detectorValue, multiplier);

            if (!isTableInitialized) {
                extendedTable.add(extendedTable.size() - 1, virtualPoint);
                adapter.updateData(extendedTable);
            } else {
                adapter.updateVirtualPoint(virtualPoint);
            }
        }
    }

    private float parsePressure(String pressure) {
        return ZERO.equals(pressure) ? 0f : Float.parseFloat(pressure);
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
        setUpInstallationPoint(sensorConfig);
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
        binding.messageDeliveryPeriodEditText.addTextChangedListener(createWatcher(value
                -> sensorConfig.setMessageDeliveryPeriod(toInt(value))));
        binding.measurementPeriodEditText.addTextChangedListener(createWatcher(value
                -> sensorConfig.setMeasurementPeriod(toInt(value))));
        binding.stateNumber.addTextChangedListener(createWatcher(sensorConfig::setStateNumber));
    }

    private void setUpInstallationPoint(SensorConfig sensorConfig) {
        List<String> pointOptions = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            pointOptions.add(getInstallationPointDescription(i));
        }

        ArrayAdapter<String> pointAdapter = new ArrayAdapter<>(binding.getRoot().getContext(), android.R.layout.simple_spinner_item, pointOptions);
        pointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.installationPointSpinner.setAdapter(pointAdapter);

        int installationPoint = sensorConfig.getInstallationPoint();
        String description = getInstallationPointDescription(installationPoint);
        int spinnerPosition = pointAdapter.getPosition(description);
        binding.installationPointSpinner.setSelection(spinnerPosition);

        binding.installationPointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sensorConfig.setInstallationPoint(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private int toInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String getInstallationPointDescription(int installationPoint) {
        int axle = (installationPoint - 1) / 2 + 1;
        String position = (installationPoint - 1) % 2 == 0 ? LEFT : RIGHT;
        return AXLE + " " + axle + " — " + position;
    }

    private TextWatcher createWatcher(Consumer<String> onChanged) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onChanged.accept(s.toString());
            }
        };
    }
}
