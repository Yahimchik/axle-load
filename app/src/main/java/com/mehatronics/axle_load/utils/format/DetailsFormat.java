package com.mehatronics.axle_load.utils.format;

import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToValue;

import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;

import java.util.Objects;

public class DetailsFormat {
    public static String setDeviceName(DeviceDetails deviceDetails) {
        return deviceDetails.getDeviceName();
    }

    public static String setFirmwareVersion(DeviceDetails deviceDetails) {
        return deviceDetails.getFirmwareVersion();
    }

    public static String setHardWareVersion(DeviceDetails deviceDetails) {
        return deviceDetails.getHardWareVersion();
    }

    public static String setBatteryLevel(DeviceDetails deviceDetails) {
        return deviceDetails.getBatteryLevel() + " %";
    }

    public static String setWeight(DeviceDetails deviceDetails) {
        return deviceDetails.getWeight() + " Kg";
    }

    public static String setWeight(Device device) {
        byte[] bytes = Objects.requireNonNull(device.getScanResult().getScanRecord()).getBytes();
        return String.valueOf(convertBytesToValue(bytes, 23, 24));
    }

    public static String setPressure(DeviceDetails deviceDetails) {
        return deviceDetails.getPressure() + " kPa";
    }

    public static String setPressure(Device device) {
        byte[] bytes = Objects.requireNonNull(device.getScanResult().getScanRecord()).getBytes();
        return String.valueOf(convertBytesToValue(bytes, 21, 22) / 10f);
    }
}
