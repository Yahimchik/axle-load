package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND_DECIMAL;

import android.util.Log;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDate;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.List;

public class ByteUtils {
    private static float intToFloat(int integer) {
        return Float.intBitsToFloat(integer);
    }

    private static int parseIntFromBytes(byte[] bytes, int x1, int x2) {
        int iTemp = 0;
        iTemp |= bytes[x1] & ZERO_COMMAND_BINARY;
        iTemp <<= 8;
        iTemp |= bytes[x2] & ZERO_COMMAND_BINARY;
        return iTemp;
    }

    private static int convertToDetector(byte[] bytes, int i) {
        int detector;
        detector = 0;
        detector |= bytes[i * 6 + 5] & ZERO_COMMAND_BINARY;
        detector <<= 8;
        detector |= bytes[i * 6 + 4] & ZERO_COMMAND_BINARY;
        return detector;
    }

    private static int convertToMultiplier(byte[] bytes, int i) {
        int multiplier = parseIntFromBytes(bytes, i * 6 + 9, i * 6 + 8);
        multiplier <<= 8;
        multiplier |= bytes[i * 6 + 7] & ZERO_COMMAND_BINARY;
        multiplier <<= 8;
        multiplier |= bytes[i * 6 + 6] & ZERO_COMMAND_BINARY;
        return multiplier;
    }

    public static int convertByteToValue(byte[] bytes, int first, int second) {
        return parseIntFromBytes(bytes, first, second);
    }

    public static int convertBytesToValue(byte[] bytes, int first, int second) {
        return (bytes[first] & ZERO_COMMAND_BINARY) * 256 + (bytes[second] & ZERO_COMMAND_BINARY);
    }

    private static int getYearFromTwoBytes(byte[] bytes) {
        int year = bytes[1] & ZERO_COMMAND_BINARY;
        year = year << 8;
        return year + (bytes[0] & ZERO_COMMAND_BINARY);
    }

    private static short parseShortFromBytes(byte[] bytes, int x1, int x2) {
        short iTempShort = 0;
        iTempShort |= (short) (bytes[x1] & ZERO_COMMAND_BINARY);
        iTempShort <<= 8;
        iTempShort |= (short) (bytes[x2] & ZERO_COMMAND_BINARY);
        return iTempShort;
    }

    public static void convertBytesToCalibrationTable(byte[] bytes, List<CalibrationTable> table) {
        if ((bytes[0] & ZERO_COMMAND_BINARY) == FIRST_COMMAND) {
            if ((bytes[1] & ZERO_COMMAND_BINARY) == ZERO_COMMAND_DECIMAL) {
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
    
    public static SensorConfig convertBytesToConfiguration(byte[] bytes) {
        return new SensorConfig.Builder()
                .setMessageDeliveryPeriod(parseIntFromBytes(bytes, 23, 22))
                .setMeasurementPeriod(parseIntFromBytes(bytes, 25, 24))
                .setDistanceBetweenAxlesOneTwoMm(parseShortFromBytes(bytes, 27, 26))
                .setDistanceBetweenAxlesTwoThreeMm(parseShortFromBytes(bytes, 29, 28))
                .setDistanceToWheel(parseShortFromBytes(bytes, 31, 30))
                .build();
    }
    
    public static DeviceDate getDate(byte[] bytes) {
        if (bytes.length == 7) {
            return createDeviceDate(bytes[3], bytes[2], getYearFromTwoBytes(bytes));
        } else if (bytes.length >= 3) {
            return createDeviceDate(bytes[2], bytes[1], bytes[0]);
        }
        return new DeviceDate.Builder().build();
    }

    private static DeviceDate createDeviceDate(int year, int month, int day) {
        return new DeviceDate.Builder()
                .addYear(year)
                .addMonth(month)
                .addDay(day)
                .build();
    }
}
