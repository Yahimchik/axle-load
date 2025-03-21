package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND;

import com.mehatronics.axle_load.entities.DeviceDate;

public class ByteUtils {
    public static int convertByteToValue(byte[] bytes, int first, int second) {
        int value = 0;
        value |= bytes[first] & ZERO_COMMAND;
        value <<= 8;
        value |= bytes[second] & ZERO_COMMAND;
        return value;
    }

    public static int convertBytesToValue(byte[] bytes, int first, int second) {
        return (bytes[first] & ZERO_COMMAND) * 256 + (bytes[second] & ZERO_COMMAND);
    }

    public static DeviceDate getDate(byte[] bytes) {
        if (bytes.length == 7) {
            return createDeviceDate(bytes[3], bytes[2], getYearFromTwoBytes(bytes));
        } else if (bytes.length >= 3) {
            return createDeviceDate(bytes[2], bytes[1], bytes[0]);
        }
        return new DeviceDate.Builder().build();
    }

    private static int getYearFromTwoBytes(byte[] bytes) {
        int year = bytes[1] & ZERO_COMMAND;
        year = year << 8;
        return year + (bytes[0] & ZERO_COMMAND);
    }

    private static DeviceDate createDeviceDate(int year, int month, int day) {
        return new DeviceDate.Builder()
                .addYear(year)
                .addMonth(month)
                .addDay(day)
                .build();
    }
}
