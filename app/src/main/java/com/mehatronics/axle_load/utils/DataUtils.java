package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.ByteUtils.convertByteToValue;
import static com.mehatronics.axle_load.utils.ByteUtils.getDate;
import static com.mehatronics.axle_load.utils.DateFormatUtils.convertToDateFormat;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SECOND_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.utils.constants.StringConstants.UNKNOWN;
import static com.mehatronics.axle_load.utils.constants.StringConstants.ZERO;

import com.mehatronics.axle_load.entities.enums.CharacteristicType;

public class DataUtils {

    public static String convertBytesToString(byte[] bytes){
        return new String(bytes);
    }

    public static String convertBytesToBattery(byte[] bytes) {
        if (bytes != null && bytes.length <= 3) {
            return String.valueOf(bytes[0]);
        }
        return ZERO;
    }

    public static String convertBytesToDate(byte[] bytes) {
        if (bytes != null) {
            return convertToDateFormat(getDate(bytes));
        }
        return UNKNOWN;
    }

    public static String convertBytesToValue(byte[] bytes, CharacteristicType type) {
        if ((bytes[0] & ZERO_COMMAND_BINARY) == SEVEN_COMMAND) {
            if ((bytes[1] & ZERO_COMMAND_BINARY) == SECOND_COMMAND) {
                if (type.equals(WEIGHT)) {
                    return String.valueOf(convertByteToValue(bytes, 7, 6));
                }

                if (type.equals(PRESSURE)) {
                    return String.valueOf(convertByteToValue(bytes, 5, 4) / 10f);
                }
            }
        }
        return ZERO;
    }
}

