package com.mehatronics.axle_load.ui;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;
import com.mehatronics.axle_load.entities.DeviceDetails;

import java.util.ArrayList;

public class DeviceDetailsBinder {
    private final View view;
    private final CalibrationTableAdapter adapter;

    public DeviceDetailsBinder(View view) {
        this.view = view;
        RecyclerView recyclerView = view.findViewById(R.id.calibrationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new CalibrationTableAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    public void bind(DeviceDetails deviceDetails) {
        TextView deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
        TextView firmwareVersionTextView = view.findViewById(R.id.firmwareVersionTextView);
        TextView hardwareVersionTextView = view.findViewById(R.id.hardwareVersionTextView);
        TextView batteryLevelTextView = view.findViewById(R.id.batteryLevelTextView);
        TextView weightTextView = view.findViewById(R.id.weightTextView);
        TextView pressureTextView = view.findViewById(R.id.pressureTextView);

        TextView messageDeliveryPeriodTextView = view.findViewById(R.id.messageDeliveryPeriodEditText);
        TextView measurementPeriodTextView = view.findViewById(R.id.measurementPeriodEditText);
        TextView distanceBetweenAxlesOneTwoTextView = view.findViewById(R.id.distanceBetweenAxlesOneTwoEditText);
        TextView distanceBetweenAxlesTwoThreeTextView = view.findViewById(R.id.distanceBetweenAxlesTwoThreeEditText);
        TextView distanceToWheelTextView = view.findViewById(R.id.distanceToWheelEditText);

        deviceNameTextView.setText(deviceDetails.getDeviceName());
        firmwareVersionTextView.setText("Версия ПО: " + deviceDetails.getFirmwareVersion());
        hardwareVersionTextView.setText("Версия железа: " + deviceDetails.getHardWareVersion());
        batteryLevelTextView.setText("Батарея: " + deviceDetails.getBatteryLevel() + " %");
        weightTextView.setText("Вес: " + deviceDetails.getWeight() + " Kg");
        pressureTextView.setText("Давление: " + deviceDetails.getPressure() + " kPa");

        messageDeliveryPeriodTextView.setText("Message delivery period, sec " + deviceDetails.getSensorConfig().getMessageDeliveryPeriod());
        measurementPeriodTextView.setText("Measurement period, sec " + deviceDetails.getSensorConfig().getMeasurementPeriod());
        distanceBetweenAxlesOneTwoTextView.setText("Distance between axles 1-2, mm " + deviceDetails.getSensorConfig().getDistanceBetweenAxlesOneTwoMm());
        distanceBetweenAxlesTwoThreeTextView.setText("Distance between axles 2-3, mm " + deviceDetails.getSensorConfig().getDistanceBetweenAxlesTwoThreeMm());
        distanceToWheelTextView.setText("Distance to fifth wheel coupling, mm " + deviceDetails.getSensorConfig().getDistanceToWheel());

        adapter.updateData(deviceDetails.getTable());
    }
}
