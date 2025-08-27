package com.mehatronics.axle_load.data.format;


import com.mehatronics.axle_load.domain.entities.SensorConfig;

public interface SensorConfigFormatter {
    String formatMessageDeliveryPeriod(SensorConfig sensorConfig);
    String formatMeasurementPeriod(SensorConfig sensorConfig);
    String formatStateNumber(SensorConfig sensorConfig);
    String formatDistanceBetweenAxlesOneTwoMm(SensorConfig sensorConfig);
    String formatDistanceBetweenAxlesTwoThreeMm(SensorConfig sensorConfig);
    String formatDistanceToWheel(SensorConfig sensorConfig);
}