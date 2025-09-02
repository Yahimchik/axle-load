package com.mehatronics.axle_load.ui.adapter.sensor;

import static com.mehatronics.axle_load.constants.ValueConstants.DBM_DELAY;

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
    private final TextView deviceTypeTextView;
    private final TextView signalValueTextView;
    private final TextView pressureTextView;
    private final TextView weightTextView;
    private final TextView deviceMac;

    private final Button readFromSensorButton;
    private final Button saveToFileButton;
    private final Button readFromFileButton;

    private String cachedFirmwareVersion;
    private String cachedHardwareVersion;
    private String cachedBatteryLevel;
    private String cachedDeviceName;
    private String cachedDeviceMac;
    private String cachedPressure;
    private String cachedWeight;
    private String cachedType;
    private String cashedRssi;

    public SensorInfoAdapter(View root, DeviceDetailsFormatter formatter) {
        signalValueTextView = root.findViewById(R.id.signalValueTextView);
        deviceTypeTextView = root.findViewById(R.id.deviceTypeTextView);
        deviceNameTextView = root.findViewById(R.id.deviceSerialTextView);
        deviceMac = root.findViewById(R.id.deviceMacTextView);
        firmwareVersionTextView = root.findViewById(R.id.firmwareVersionValueTextView);
        hardwareVersionTextView = root.findViewById(R.id.hardwareVersionValueTextView);
        batteryLevelTextView = root.findViewById(R.id.batteryLevelValueTextView);
        weightTextView = root.findViewById(R.id.weightValueTextView);
        pressureTextView = root.findViewById(R.id.pressureValueTextView);
        readFromSensorButton = root.findViewById(R.id.readFromSensorButton);
        saveToFileButton = root.findViewById(R.id.saveToFileButton);
        readFromFileButton = root.requireViewById(R.id.readFromFileButton);
        this.formatter = formatter;
    }


    public void saveToFileOnClick(View.OnClickListener listener){
        saveToFileButton.setOnClickListener(listener);
    }

    public void readFromFileButton(View.OnClickListener listener){
        readFromFileButton.setOnClickListener(listener);
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
        updateTextIfChanged(deviceTypeTextView, formatter.formatDeviceType(newDetails),cachedType, val -> cachedType = val);

        updateRssiThrottled(signalValueTextView, formatter.formatRssi(newDetails), cashedRssi, val -> cashedRssi = val);
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

    private long lastRssiUpdateTime = 0;

    private void updateRssiThrottled(TextView view, String newValue, String cachedValue, Consumer<String> cacheSetter) {
        if ("0 dBm".equals(newValue)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastRssiUpdateTime < DBM_DELAY) {
            return;
        }
        if (!newValue.equals(cachedValue)) {
            view.setText(newValue);
            cacheSetter.accept(newValue);
            lastRssiUpdateTime = now;
        }
    }
}