package com.mehatronics.axle_load.entities;

import androidx.annotation.NonNull;

public class SensorConfig {
    private final int messageDeliveryPeriod;
    private final int measurementPeriod;
    private final int distanceBetweenAxlesOneTwoMm;
    private final int distanceBetweenAxlesTwoThreeMm;
    private final int distanceToWheel;

    public SensorConfig(Builder builder) {
        this.messageDeliveryPeriod = builder.messageDeliveryPeriod;
        this.measurementPeriod = builder.measurementPeriod;
        this.distanceBetweenAxlesOneTwoMm = builder.distanceBetweenAxlesOneTwoMm;
        this.distanceBetweenAxlesTwoThreeMm = builder.distanceBetweenAxlesTwoThreeMm;
        this.distanceToWheel = builder.distanceToWheel;
    }

    public int getMessageDeliveryPeriod() {
        return messageDeliveryPeriod;
    }

    public int getMeasurementPeriod() {
        return measurementPeriod;
    }

    public int getDistanceBetweenAxlesOneTwoMm() {
        return distanceBetweenAxlesOneTwoMm;
    }

    public int getDistanceBetweenAxlesTwoThreeMm() {
        return distanceBetweenAxlesTwoThreeMm;
    }

    public int getDistanceToWheel() {
        return distanceToWheel;
    }

    @NonNull
    @Override
    public String toString() {
        return "SensorConfig{" +
                "messageDeliveryPeriod=" + messageDeliveryPeriod +
                ", measurementPeriod=" + measurementPeriod +
                ", distanceBetweenAxlesOneTwoMm=" + distanceBetweenAxlesOneTwoMm +
                ", distanceBetweenAxlesTwoThreeMm=" + distanceBetweenAxlesTwoThreeMm +
                ", distanceToWheel=" + distanceToWheel +
                '}';
    }

    public static class Builder {
        private int messageDeliveryPeriod;
        private int measurementPeriod;
        private int distanceBetweenAxlesOneTwoMm;
        private int distanceBetweenAxlesTwoThreeMm;
        private int distanceToWheel;

        public Builder setMessageDeliveryPeriod(int messageDeliveryPeriod) {
            this.messageDeliveryPeriod = messageDeliveryPeriod;
            return this;
        }

        public Builder setMeasurementPeriod(int measurementPeriod) {
            this.measurementPeriod = measurementPeriod;
            return this;
        }

        public Builder setDistanceBetweenAxlesOneTwoMm(int distanceBetweenAxlesOneTwoMm) {
            this.distanceBetweenAxlesOneTwoMm = distanceBetweenAxlesOneTwoMm;
            return this;
        }

        public Builder setDistanceBetweenAxlesTwoThreeMm(int distanceBetweenAxlesTwoThreeMm) {
            this.distanceBetweenAxlesTwoThreeMm = distanceBetweenAxlesTwoThreeMm;
            return this;
        }

        public Builder setDistanceToWheel(int distanceToWheel) {

            this.distanceToWheel = distanceToWheel;
            return this;
        }

        public SensorConfig build() {
            return new SensorConfig(this);
        }
    }
}
