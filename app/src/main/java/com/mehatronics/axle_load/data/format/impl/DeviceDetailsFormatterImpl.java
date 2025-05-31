package com.mehatronics.axle_load.data.format.impl;

import com.mehatronics.axle_load.data.format.DeviceDetailsFormatter;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;

import javax.inject.Inject;

public class DeviceDetailsFormatterImpl implements DeviceDetailsFormatter {

    @Inject
    public DeviceDetailsFormatterImpl() {
    }

    @Override
    public String formatDeviceName(DeviceDetails details) {
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
