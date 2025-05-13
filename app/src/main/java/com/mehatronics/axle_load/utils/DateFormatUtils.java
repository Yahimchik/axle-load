package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.constants.FormatConstants.DATE_FORMAT;

import static java.util.Locale.getDefault;

import androidx.annotation.NonNull;

import com.mehatronics.axle_load.entities.DeviceDate;


public class DateFormatUtils {
    @NonNull
    public static String convertToDateFormat(DeviceDate date) {
        return String.format(
                getDefault(),
                DATE_FORMAT,
                date.getYear(),
                date.getMonth(),
                date.getDay()
        );
    }
}
