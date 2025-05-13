package com.mehatronics.axle_load.format;


public interface SensorConfigFormatter<T> {
    String formatMessageDeliveryPeriod(T config);

    String formatMeasurementPeriod(T config);

    String formatStateNumber(T config);

    String formatDistanceBetweenAxlesOneTwo(T config);

    String formatDistanceBetweenAxlesTwoThree(T config);

    String formatDistanceToWheel(T config);
}
