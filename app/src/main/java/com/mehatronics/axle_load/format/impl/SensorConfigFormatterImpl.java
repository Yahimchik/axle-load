package com.mehatronics.axle_load.format.impl;


import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.format.SensorConfigFormatter;

public class SensorConfigFormatterImpl implements SensorConfigFormatter<SensorConfig> {

    @Override
    public String formatMessageDeliveryPeriod(SensorConfig config) {
        return String.valueOf(config.getMessageDeliveryPeriod());
    }

    @Override
    public String formatMeasurementPeriod(SensorConfig config) {
        return String.valueOf(config.getMeasurementPeriod());
    }

    @Override
    public String formatStateNumber(SensorConfig config) {
        return config.getStateNumber();
    }

    @Override
    public String formatDistanceBetweenAxlesOneTwo(SensorConfig config) {
        return String.valueOf(config.getDistanceBetweenAxlesOneTwoMm());
    }

    @Override
    public String formatDistanceBetweenAxlesTwoThree(SensorConfig config) {
        return String.valueOf(config.getDistanceBetweenAxlesTwoThreeMm());
    }

    @Override
    public String formatDistanceToWheel(SensorConfig config) {
        return String.valueOf(config.getDistanceToWheel());
    }
}
