package com.mehatronics.axle_load.utils.format;

import com.mehatronics.axle_load.entities.SensorConfig;

public class SensorConfigFormat {
    public static String setMessageDeliveryPeriod(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getMessageDeliveryPeriod());
    }

    public static String setMeasurementPeriod(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getMeasurementPeriod());
    }

    public static String setStateNumber(SensorConfig sensorConfig){
        return sensorConfig.getStateNumber();
    }

    public static String setDistanceBetweenAxlesOneTwoMm(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getDistanceBetweenAxlesOneTwoMm());
    }

    public static String setDistanceBetweenAxlesTwoThreeMm(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getDistanceBetweenAxlesTwoThreeMm());
    }

    public static String setDistanceToWheel(SensorConfig sensorConfig) {
        return String.valueOf(sensorConfig.getDistanceToWheel());
    }
}
