package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DeviceDetailsBinder {
    private final View view;
    private final CalibrationTableAdapter adapter;

    private final EditText messageDeliveryPeriod;
    private final EditText measurementPeriod;
    private final EditText stateNumber;

    private final TextView firmwareVersionTextView;
    private final TextView hardwareVersionTextView;
    private final TextView batteryLevelTextView;
    private final TextView deviceNameTextView;
    private final TextView weightTextView;
    private final TextView pressureTextView;

    private final Spinner installationPointSpinner;
    private final Button saveButton;


    public DeviceDetailsBinder(View view) {
        this.view = view;

        adapter = new CalibrationTableAdapter(new ArrayList<>());
        initRecyclerView(view, R.id.calibrationRecyclerView, adapter);

        messageDeliveryPeriod = view.findViewById(R.id.messageDeliveryPeriodEditText);
        measurementPeriod = view.findViewById(R.id.measurementPeriodEditText);
        stateNumber = view.findViewById(R.id.stateNumber);

        installationPointSpinner = view.findViewById(R.id.installationPointSpinner);
        saveButton = view.findViewById(R.id.saveConfigurationButton);

        deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
        firmwareVersionTextView = view.findViewById(R.id.firmwareVersionValueTextView);
        hardwareVersionTextView = view.findViewById(R.id.hardwareVersionValueTextView);

        batteryLevelTextView = view.findViewById(R.id.batteryLevelValueTextView);
        weightTextView = view.findViewById(R.id.weightValueTextView);
        pressureTextView = view.findViewById(R.id.pressureValueTextView);
    }

    @SuppressLint("SetTextI18n")
    public void bind(DeviceDetails deviceDetails) {
        deviceNameTextView.setText(setDeviceName(deviceDetails));
        firmwareVersionTextView.setText(setFirmwareVersion(deviceDetails));
        hardwareVersionTextView.setText(setHardWareVersion(deviceDetails));

        batteryLevelTextView.setText(setBatteryLevel(deviceDetails));
        weightTextView.setText(setWeight(deviceDetails));
        pressureTextView.setText(setPressure(deviceDetails));

        adapter.updateData(deviceDetails.getTable());
    }

    @SuppressLint("SetTextI18n")
    public void bindConfigure(SensorConfig sensorConfig) {
        setUpInstallationPoint(sensorConfig);
        populateSensorFields(sensorConfig);
        attachSensorListeners(sensorConfig);
    }

    public void setupSaveButton(View.OnClickListener listener) {
        saveButton.setOnClickListener(listener);
    }

    private void populateSensorFields(SensorConfig sensorConfig) {
        messageDeliveryPeriod.setText(setMessageDeliveryPeriod(sensorConfig));
        measurementPeriod.setText(setMeasurementPeriod(sensorConfig));
        stateNumber.setText(setStateNumber(sensorConfig));
    }

    private void attachSensorListeners(SensorConfig sensorConfig) {
        messageDeliveryPeriod.addTextChangedListener(createWatcher(value
                -> sensorConfig.setMessageDeliveryPeriod(toInt(value))));
        measurementPeriod.addTextChangedListener(createWatcher(value
                -> sensorConfig.setMeasurementPeriod(toInt(value))));
        stateNumber.addTextChangedListener(createWatcher(sensorConfig::setStateNumber));
    }

    private void setUpInstallationPoint(SensorConfig sensorConfig) {
        List<String> pointOptions = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            pointOptions.add(getInstallationPointDescription(i));
        }

        ArrayAdapter<String> pointAdapter = new ArrayAdapter<>(
                view.getContext(), android.R.layout.simple_spinner_item, pointOptions
        );
        pointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        installationPointSpinner.setAdapter(pointAdapter);

        int installationPoint = sensorConfig.getInstallationPoint();
        String description = getInstallationPointDescription(installationPoint);
        int spinnerPosition = pointAdapter.getPosition(description);
        installationPointSpinner.setSelection(spinnerPosition);

        installationPointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        String position = (installationPoint - 1) % 2 == 0 ? "Лево" : "Право";
        return "Ось " + axle + " — " + position;
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
