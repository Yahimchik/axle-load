package com.mehatronics.axle_load.data.mapper.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.constants.FormatConstants.DATE_FORMAT;

import static java.lang.String.format;
import static java.util.Locale.getDefault;

import com.mehatronics.axle_load.data.mapper.DateFormatMapper;
import com.mehatronics.axle_load.domain.entities.device.DeviceDate;

import java.util.List;

import javax.inject.Inject;


public class DateFormatMapperImpl implements DateFormatMapper {
    public static final int DATE_INDEX = 3;
    @Inject
    public DateFormatMapperImpl() {
    }

    @Override
    public String convertToDate(List<byte[]> buffer) {
        DeviceDate date = getDate(buffer.get(DATE_INDEX));
        return format(getDefault(), DATE_FORMAT, date.getYear(), date.getMonth(), date.getDay());
    }

    private DeviceDate getDate(byte[] bytes) {
        if (bytes.length == 7) {
            return createDeviceDate(bytes[3], bytes[2], getYearFromTwoBytes(bytes));
        } else if (bytes.length >= 3) {
            return createDeviceDate(bytes[2], bytes[1], bytes[0]);
        }
        return new DeviceDate.Builder().build();
    }

    private DeviceDate createDeviceDate(int year, int month, int day) {
        return new DeviceDate.Builder()
                .addYear(year)
                .addMonth(month)
                .addDay(day)
                .build();
    }

    private int getYearFromTwoBytes(byte[] bytes) {
        return ((bytes[1] & ZERO_COMMAND_BINARY) << 8) |
                (bytes[0] & ZERO_COMMAND_BINARY);
    }
}
