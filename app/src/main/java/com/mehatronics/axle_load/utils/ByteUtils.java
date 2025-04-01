package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND_BINARY;

import android.util.Log;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDate;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.List;

public class ByteUtils {

    public static SensorConfig convertBytesToConfiguration(byte[] bytes) {
        return new SensorConfig.Builder()
                .setMessageDeliveryPeriod(extracted(bytes, 23, 0xff, 22))
                .setMeasurementPeriod(extracted(bytes, 25, 0xff, 24))
                .setDistanceBetweenAxlesOneTwoMm(extracted1(bytes, 27, 26))
                .setDistanceBetweenAxlesTwoThreeMm(extracted1(bytes, 29, 28))
                .setDistanceToWheel(extracted1(bytes, 31, 30))
                .build();
    }

    private static short extracted1(byte[] bytes, int x1, int x2) {
        short iTempShort = 0;
        iTempShort |= (short) (bytes[x1] & 0xff);
        iTempShort <<= 8;
        iTempShort |= (short) (bytes[x2] & 0xff);
        return iTempShort;
    }

    private static int extracted(byte[] bytes, int x, int x1, int x2) {
        int iTemp = 0;
        iTemp |= bytes[x] & x1;
        iTemp <<= 8;
        iTemp |= bytes[x2] & x1;
        return iTemp;
    }

    public static void convertBytesToCalibrationTable(byte[] bytes, List<CalibrationTable> table) {
        if ((bytes[0] & 0xff) == 0x01) {
            if ((bytes[1] & 0xff) == 0x00) {
                for (int i = 0; i < 9; i++) {
                    int detector = convertToDetector(bytes, i);

                    if (detector == 0) {
                        break;
                    }

                    int multiplier = convertToMultiplier(bytes, i);
                    float tableEnd = intToFloat(multiplier);

                    Log.d("MyTag", String.valueOf(new CalibrationTable(detector, tableEnd)));
                    //TODO: It is necessary to insert a check for the completeness of the reading data somewhere.
                    table.add(new CalibrationTable(detector, Float.intBitsToFloat(multiplier)));
                    if (tableEnd == 1000000.0F) {
                        break;
                    }
                }
            }
        }
    }

    private static float intToFloat(int integer) {
        return Float.intBitsToFloat(integer);
    }

    private static int convertToDetector(byte[] bytes, int i) {
        int detector;
        detector = 0;
        detector |= bytes[i * 6 + 5] & 0xff;
        detector <<= 8;
        detector |= bytes[i * 6 + 4] & 0xff;
        return detector;
    }

    private static int convertToMultiplier(byte[] bytes, int i) {
        int multiplier = extracted(bytes, i * 6 + 9, 0xff, i * 6 + 8);
        multiplier <<= 8;
        multiplier |= bytes[i * 6 + 7] & 0xff;
        multiplier <<= 8;
        multiplier |= bytes[i * 6 + 6] & 0xff;
        return multiplier;
    }

    public static int convertByteToValue(byte[] bytes, int first, int second) {
        int value = extracted(bytes, first, ZERO_COMMAND_BINARY, second);
        return value;
    }

    public static int convertBytesToValue(byte[] bytes, int first, int second) {
        return (bytes[first] & ZERO_COMMAND_BINARY) * 256 + (bytes[second] & ZERO_COMMAND_BINARY);
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
        int year = bytes[1] & ZERO_COMMAND_BINARY;
        year = year << 8;
        return year + (bytes[0] & ZERO_COMMAND_BINARY);
    }

    private static DeviceDate createDeviceDate(int year, int month, int day) {
        return new DeviceDate.Builder()
                .addYear(year)
                .addMonth(month)
                .addDay(day)
                .build();
    }
}
