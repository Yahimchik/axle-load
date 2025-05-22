package com.mehatronics.axle_load.adapter.sensor;

import static com.mehatronics.axle_load.utils.format.DetailsFormat.setBatteryLevel;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setDeviceName;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setFirmwareVersion;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setHardWareVersion;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setPressure;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setWeight;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.DeviceDetails;

import java.util.function.Consumer;

public class SensorInfoAdapter {
    private final TextView deviceNameTextView;
    private final TextView firmwareVersionTextView;
    private final TextView hardwareVersionTextView;
    private final TextView batteryLevelTextView;
    private final TextView weightTextView;
    private final TextView pressureTextView;
    private final Button readFromSensorButton;
    private final Button saveTableButton;

    private String cachedDeviceName;
    private String cachedFirmwareVersion;
    private String cachedHardwareVersion;
    private String cachedBatteryLevel;
    private String cachedWeight;
    private String cachedPressure;

    public SensorInfoAdapter(View root) {
        deviceNameTextView = root.findViewById(R.id.deviceNameTextView);
        firmwareVersionTextView = root.findViewById(R.id.firmwareVersionValueTextView);
        hardwareVersionTextView = root.findViewById(R.id.hardwareVersionValueTextView);
        batteryLevelTextView = root.findViewById(R.id.batteryLevelValueTextView);
        weightTextView = root.findViewById(R.id.weightValueTextView);
        pressureTextView = root.findViewById(R.id.pressureValueTextView);
        readFromSensorButton = root.findViewById(R.id.readFromSensorButton);
        saveTableButton = root.findViewById(R.id.saveTableButton);

    }

    public void bind(DeviceDetails newDetails) {
        if (newDetails == null) return;
        updateTextIfChanged(deviceNameTextView, setDeviceName(newDetails), cachedDeviceName,
                val -> cachedDeviceName = val);

        updateTextIfChanged(firmwareVersionTextView, setFirmwareVersion(newDetails), cachedFirmwareVersion,
                val -> cachedFirmwareVersion = val);

        updateTextIfChanged(hardwareVersionTextView, setHardWareVersion(newDetails), cachedHardwareVersion,
                val -> cachedHardwareVersion = val);

        updateTextIfChanged(batteryLevelTextView, setBatteryLevel(newDetails), cachedBatteryLevel,
                val -> cachedBatteryLevel = val);

        updateTextIfChanged(weightTextView, setWeight(newDetails), cachedWeight,
                val -> cachedWeight = val);

        updateTextIfChanged(pressureTextView, setPressure(newDetails), cachedPressure,
                val -> cachedPressure = val);
    }

    private void updateTextIfChanged(TextView view, String newValue, String cachedValue, Consumer<String> cacheSetter) {
        if (!newValue.equals(cachedValue)) {
            view.setText(newValue);
            cacheSetter.accept(newValue);
        }
    }

    public void setReadFromSensorButtonClickListener(View.OnClickListener listener) {
        readFromSensorButton.setOnClickListener(listener);
    }

    public void setSaveTableButton(View.OnClickListener listener) {
        saveTableButton.setOnClickListener(listener);
    }
}

