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

    public DeviceDetailsBinder(View view) {
        this.view = view;
        adapter = new CalibrationTableAdapter(new ArrayList<>());
        initRecyclerView(view, R.id.calibrationRecyclerView, adapter);
    }

    @SuppressLint("SetTextI18n")
    public void bindConfigure(SensorConfig sensorConfig) {
        EditText messageDeliveryPeriod = view.findViewById(R.id.messageDeliveryPeriodEditText);
        EditText measurementPeriod = view.findViewById(R.id.measurementPeriodEditText);
        EditText stateNumber = view.findViewById(R.id.stateNumber);

        Spinner installationPointSpinner = view.findViewById(R.id.installationPointSpinner);

        List<String> pointOptions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
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

        messageDeliveryPeriod.setText(setMessageDeliveryPeriod(sensorConfig));
        measurementPeriod.setText(setMeasurementPeriod(sensorConfig));
        stateNumber.setText(setStateNumber(sensorConfig));

        Log.d("MyTag", "Installation point: " + sensorConfig.getInstallationPoint());

        messageDeliveryPeriod.addTextChangedListener(createWatcher(value ->
                sensorConfig.setMessageDeliveryPeriod(toInt(value))));

        measurementPeriod.addTextChangedListener(createWatcher(value ->
                sensorConfig.setMeasurementPeriod(toInt(value))));

        stateNumber.addTextChangedListener(createWatcher(sensorConfig::setStateNumber));
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

    @SuppressLint("SetTextI18n")
    public void bind(DeviceDetails deviceDetails) {
        TextView deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
        TextView firmwareVersionTextView = view.findViewById(R.id.firmwareVersionValueTextView);
        TextView hardwareVersionTextView = view.findViewById(R.id.hardwareVersionValueTextView);
        TextView batteryLevelTextView = view.findViewById(R.id.batteryLevelValueTextView);
        TextView weightTextView = view.findViewById(R.id.weightValueTextView);
        TextView pressureTextView = view.findViewById(R.id.pressureValueTextView);

        deviceNameTextView.setText(setDeviceName(deviceDetails));
        firmwareVersionTextView.setText(setFirmwareVersion(deviceDetails));
        hardwareVersionTextView.setText(setHardWareVersion(deviceDetails));
        batteryLevelTextView.setText(setBatteryLevel(deviceDetails));
        weightTextView.setText(setWeight(deviceDetails));
        pressureTextView.setText(setPressure(deviceDetails));

        adapter.updateData(deviceDetails.getTable());
    }
}
