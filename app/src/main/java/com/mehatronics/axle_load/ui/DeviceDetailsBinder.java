package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.ArrayList;

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
        TextView messageDeliveryPeriodTextView = view.findViewById(R.id.messageDeliveryPeriodEditText);
        TextView measurementPeriodTextView = view.findViewById(R.id.measurementPeriodEditText);
        TextView distanceBetweenAxlesOneTwoTextView = view.findViewById(R.id.distanceBetweenAxlesOneTwoEditText);
        TextView distanceBetweenAxlesTwoThreeTextView = view.findViewById(R.id.distanceBetweenAxlesTwoThreeEditText);
        TextView distanceToWheelTextView = view.findViewById(R.id.distanceToWheelEditText);

        messageDeliveryPeriodTextView.setText("Message delivery period, sec " + sensorConfig.getMessageDeliveryPeriod());
        measurementPeriodTextView.setText("Measurement period, sec " + sensorConfig.getMeasurementPeriod());
        distanceBetweenAxlesOneTwoTextView.setText("Distance between axles 1-2, mm " + sensorConfig.getDistanceBetweenAxlesOneTwoMm());
        distanceBetweenAxlesTwoThreeTextView.setText("Distance between axles 2-3, mm " + sensorConfig.getDistanceBetweenAxlesTwoThreeMm());
        distanceToWheelTextView.setText("Distance to fifth wheel coupling, mm " + sensorConfig.getDistanceToWheel());
    }

    @SuppressLint("SetTextI18n")
    public void bind(DeviceDetails deviceDetails) {
        TextView deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
        TextView firmwareVersionTextView = view.findViewById(R.id.firmwareVersionTextView);
        TextView hardwareVersionTextView = view.findViewById(R.id.hardwareVersionTextView);
        TextView batteryLevelTextView = view.findViewById(R.id.batteryLevelTextView);
        TextView weightTextView = view.findViewById(R.id.weightTextView);
        TextView pressureTextView = view.findViewById(R.id.pressureTextView);

        deviceNameTextView.setText(deviceDetails.getDeviceName());
        firmwareVersionTextView.setText("Версия ПО: " + deviceDetails.getFirmwareVersion());
        hardwareVersionTextView.setText("Версия железа: " + deviceDetails.getHardWareVersion());
        batteryLevelTextView.setText("Батарея: " + deviceDetails.getBatteryLevel() + " %");
        weightTextView.setText("Вес: " + deviceDetails.getWeight() + " Kg");
        pressureTextView.setText("Давление: " + deviceDetails.getPressure() + " kPa");

        adapter.updateData(deviceDetails.getTable());
    }

//    private void initRecyclerView() {
//        RecyclerView recyclerView = view.findViewById(R.id.calibrationRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        recyclerView.setAdapter(adapter);
//    }
}
