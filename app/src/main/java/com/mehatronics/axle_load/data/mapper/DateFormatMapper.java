package com.mehatronics.axle_load.data.mapper;

import java.util.List;

public interface DateFormatMapper {
    String convertToDate(List<byte[]> date);
}