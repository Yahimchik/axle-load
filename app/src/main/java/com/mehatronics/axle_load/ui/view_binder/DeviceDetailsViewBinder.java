package com.mehatronics.axle_load.ui.view_binder;

import com.mehatronics.axle_load.databinding.FragmentDeviceDetailsBinding;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.format.DeviceDetailsFormatter;

import javax.inject.Inject;

public class DeviceDetailsViewBinder {
    private final FragmentDeviceDetailsBinding binding;
    private final DeviceDetailsFormatter<DeviceDetails> formatter;

    @Inject
    public DeviceDetailsViewBinder(FragmentDeviceDetailsBinding binding, DeviceDetailsFormatter<DeviceDetails> formatter) {
        this.binding = binding;
        this.formatter = formatter;
    }

    public void bind(DeviceDetails details) {
        binding.deviceNameTextView.setText(formatter.formatName(details));
        binding.firmwareVersionValueTextView.setText(formatter.formatFirmwareVersion(details));
        binding.hardwareVersionValueTextView.setText(formatter.formatHardwareVersion(details));
        binding.batteryLevelValueTextView.setText(formatter.formatBatteryLevel(details));
        binding.weightValueTextView.setText(formatter.formatWeight(details));
        binding.pressureValueTextView.setText(formatter.formatPressure(details));
    }
}


