package com.mehatronics.axle_load.data.format;

import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;

public interface DeviceDetailsFormatter {
    String formatDeviceName(DeviceDetails details);
    String formatDeviceMac(DeviceDetails details);
    String formatFirmwareVersion(DeviceDetails details);
    String formatHardwareVersion(DeviceDetails details);
    String formatBatteryLevel(DeviceDetails details);
    String formatWeight(DeviceDetails details);
    String formatPressure(DeviceDetails details);
}
