package com.mehatronics.axle_load.format.impl;

import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.format.DeviceDetailsFormatter;

public class DeviceDetailsFormatterImpl implements DeviceDetailsFormatter<DeviceDetails> {

    @Override
    public String formatName(DeviceDetails details) {
        return details.getDeviceName();
    }

    @Override
    public String formatFirmwareVersion(DeviceDetails details) {
        return details.getFirmwareVersion();
    }

    @Override
    public String formatHardwareVersion(DeviceDetails details) {
        return details.getHardWareVersion();
    }

    @Override
    public String formatBatteryLevel(DeviceDetails details) {
        return details.getBatteryLevel() + " %";
    }

    @Override
    public String formatWeight(DeviceDetails details) {
        return details.getWeight() + " Kg";
    }

    @Override
    public String formatPressure(DeviceDetails details) {
        return details.getPressure() + " kPa";
    }
}
