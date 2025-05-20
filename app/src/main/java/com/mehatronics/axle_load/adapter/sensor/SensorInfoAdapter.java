package com.mehatronics.axle_load.adapter.sensor;

import static com.mehatronics.axle_load.utils.format.DetailsFormat.setBatteryLevel;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setDeviceName;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setFirmwareVersion;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setHardWareVersion;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setPressure;
import static com.mehatronics.axle_load.utils.format.DetailsFormat.setWeight;

import android.view.View;
import android.widget.TextView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SensorInfoAdapter {
    private final TextView deviceNameTextView;
    private final TextView firmwareVersionTextView;
    private final TextView hardwareVersionTextView;
    private final TextView batteryLevelTextView;
    private final TextView weightTextView;
    private final TextView pressureTextView;

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

    public boolean hasDetectorChanged(List<CalibrationTable> o, List<CalibrationTable> n) {
        if (n.size() < 2) return false;

        int index = n.size() - 2;
        if (index >= o.size()) return true;

        CalibrationTable newItem = n.get(index);
        CalibrationTable oldItem = o.get(index);

        return !Objects.equals(newItem.getDetector(), oldItem.getDetector());
    }

    private void updateTextIfChanged(TextView view, String newValue, String cachedValue, Consumer<String> cacheSetter) {
        if (!newValue.equals(cachedValue)) {
            view.setText(newValue);
            cacheSetter.accept(newValue);
        }
    }


}

