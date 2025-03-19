package com.mehatronics.axle_load.ui;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.DeviceDetails;

public class DeviceDetailsBinder {
    private final View view;

    public DeviceDetailsBinder(View view) {
        this.view = view;
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
    }
}
