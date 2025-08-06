package com.mehatronics.axle_load.ui.adapter.sensor;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.data.format.DeviceDetailsFormatter;

import java.util.function.Consumer;

public class SensorInfoAdapter {
    private final DeviceDetailsFormatter formatter;

    private final TextView firmwareVersionTextView;
    private final TextView hardwareVersionTextView;
    private final TextView batteryLevelTextView;
    private final TextView deviceNameTextView;
    private final TextView pressureTextView;
    private final TextView weightTextView;
    private final TextView deviceMac;

    private final Button readFromSensorButton;
    private final Button finishButton;
    private String cachedFirmwareVersion;
    private String cachedHardwareVersion;
    private String cachedBatteryLevel;
    private String cachedDeviceName;
    private String cachedDeviceMac;
    private String cachedPressure;
    private String cachedWeight;

    public SensorInfoAdapter(View root, DeviceDetailsFormatter formatter) {
        deviceNameTextView = root.findViewById(R.id.deviceNameTextView);
        deviceMac = root.findViewById(R.id.deviceMacTextView);
        firmwareVersionTextView = root.findViewById(R.id.firmwareVersionValueTextView);
        hardwareVersionTextView = root.findViewById(R.id.hardwareVersionValueTextView);
        batteryLevelTextView = root.findViewById(R.id.batteryLevelValueTextView);
        weightTextView = root.findViewById(R.id.weightValueTextView);
        pressureTextView = root.findViewById(R.id.pressureValueTextView);
        readFromSensorButton = root.findViewById(R.id.readFromSensorButton);
        finishButton = root.findViewById(R.id.finishButton);
        this.formatter = formatter;
    }

    public void finishButtonOnClick(View.OnClickListener listener) {
        finishButton.setOnClickListener(listener);
    }

    public void bind(DeviceDetails newDetails) {
        if (newDetails == null) return;

        updateTextIfChanged(deviceNameTextView, formatter.formatDeviceName(newDetails), cachedDeviceName, val -> cachedDeviceName = val);
        updateTextIfChanged(deviceMac, formatter.formatDeviceMac(newDetails), cachedDeviceMac, val -> cachedDeviceMac = val);

        updateTextIfChanged(firmwareVersionTextView, formatter.formatFirmwareVersion(newDetails), cachedFirmwareVersion, val -> cachedFirmwareVersion = val);
        updateTextIfChanged(hardwareVersionTextView, formatter.formatHardwareVersion(newDetails), cachedHardwareVersion, val -> cachedHardwareVersion = val);

        updateTextIfChanged(batteryLevelTextView, formatter.formatBatteryLevel(newDetails), cachedBatteryLevel, val -> cachedBatteryLevel = val);
        updateTextIfChanged(weightTextView, formatter.formatWeight(newDetails), cachedWeight, val -> cachedWeight = val);

        updateTextIfChanged(pressureTextView, formatter.formatPressure(newDetails), cachedPressure, val -> cachedPressure = val);
    }

    public void setReadFromSensorButtonClickListener(View.OnClickListener listener) {
        readFromSensorButton.setOnClickListener(listener);
    }

    private void updateTextIfChanged(TextView view, String newValue, String cachedValue, Consumer<String> cacheSetter) {
        if (!newValue.equals(cachedValue)) {
            view.setText(newValue);
            cacheSetter.accept(newValue);
        }
    }
}

