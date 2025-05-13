package com.mehatronics.axle_load.format;

public interface DeviceDetailsFormatter<T> {
    String formatName(T details);
    String formatFirmwareVersion(T details);
    String formatHardwareVersion(T details);
    String formatBatteryLevel(T details);
    String formatWeight(T details);
    String formatPressure(T details);
}
