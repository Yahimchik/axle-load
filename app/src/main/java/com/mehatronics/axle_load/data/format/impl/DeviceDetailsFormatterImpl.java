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
        return typerAndSerialNumber(details.getDeviceName())[1].trim();
    }

    @Override
    public String formatDeviceMac(DeviceDetails details) {
        return details.getDeviceMac();
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

    @Override
    public String formatRssi(DeviceDetails details) {
        return details.getRssi() + " dBm";
    }

    @Override
    public String formatDeviceType(DeviceDetails details) {
        return typerAndSerialNumber(details.getDeviceName())[0].trim();
    }

    private String[] typerAndSerialNumber(String input){
        if (input == null || !input.contains("SN:")) {
            return null;
        }

        String[] parts = input.split("SN:");
        if (parts.length < 2) {
            return null;
        }

        return parts;
    }
}