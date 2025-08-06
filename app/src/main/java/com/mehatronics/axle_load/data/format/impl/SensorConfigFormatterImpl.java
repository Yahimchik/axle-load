package com.mehatronics.axle_load.data.format.impl;

import com.mehatronics.axle_load.data.format.SensorConfigFormatter;
import com.mehatronics.axle_load.domain.entities.SensorConfig;

import javax.inject.Inject;

public class SensorConfigFormatterImpl implements SensorConfigFormatter {

    @Inject
    public SensorConfigFormatterImpl() {
    }

    @Override
    public String formatMessageDeliveryPeriod(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getMessageDeliveryPeriod());
    }

    @Override
    public String formatMeasurementPeriod(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getMeasurementPeriod());
    }

    @Override
    public String formatStateNumber(SensorConfig sensorConfig) {
        return sensorConfig.getStateNumber();
    }

    @Override
    public String formatDistanceBetweenAxlesOneTwoMm(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getDistanceBetweenAxlesOneTwoMm());
    }

    @Override
    public String formatDistanceBetweenAxlesTwoThreeMm(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getDistanceBetweenAxlesTwoThreeMm());
    }

    @Override
    public String formatDistanceToWheel(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getDistanceToWheel());
    }
}
