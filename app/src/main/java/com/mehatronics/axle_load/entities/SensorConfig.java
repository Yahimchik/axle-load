package com.mehatronics.axle_load.entities;

import androidx.annotation.NonNull;

public class SensorConfig {
    private int flagSystem;
    private int configSystem;
    private float multiplier;
    private float offset;
    private int batteryMicrovoltsPerStep;
    private int messageDeliveryPeriod;
    private int measurementPeriod;
    private int distanceBetweenAxlesOneTwoMm;
    private int distanceBetweenAxlesTwoThreeMm;
    private int distanceToWheel;
    private int configType;
    private int installationPoint;
    private String stateNumber;

    public SensorConfig(Builder builder) {
        this.flagSystem = builder.flagSystem;
        this.configSystem = builder.configSystem;
        this.multiplier = builder.multiplier;
        this.offset = builder.offset;
        this.batteryMicrovoltsPerStep = builder.batteryMicrovoltsPerStep;
        this.messageDeliveryPeriod = builder.messageDeliveryPeriod;
        this.measurementPeriod = builder.measurementPeriod;
        this.distanceBetweenAxlesOneTwoMm = builder.distanceBetweenAxlesOneTwoMm;
        this.distanceBetweenAxlesTwoThreeMm = builder.distanceBetweenAxlesTwoThreeMm;
        this.distanceToWheel = builder.distanceToWheel;
        this.configType = builder.configType;
        this.installationPoint = builder.installationPoint;
        this.stateNumber = builder.stateNumber;
    }

    public void setFlagSystem(int flagSystem) {
        this.flagSystem = flagSystem;
    }

    public void setConfigSystem(int configSystem) {
        this.configSystem = configSystem;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public void setBatteryMicrovoltsPerStep(int batteryMicrovoltsPerStep) {
        this.batteryMicrovoltsPerStep = batteryMicrovoltsPerStep;
    }

    public void setMessageDeliveryPeriod(int messageDeliveryPeriod) {
        this.messageDeliveryPeriod = messageDeliveryPeriod;
    }

    public void setMeasurementPeriod(int measurementPeriod) {
        this.measurementPeriod = measurementPeriod;
    }

    public void setDistanceBetweenAxlesOneTwoMm(int distanceBetweenAxlesOneTwoMm) {
        this.distanceBetweenAxlesOneTwoMm = distanceBetweenAxlesOneTwoMm;
    }

    public void setDistanceBetweenAxlesTwoThreeMm(int distanceBetweenAxlesTwoThreeMm) {
        this.distanceBetweenAxlesTwoThreeMm = distanceBetweenAxlesTwoThreeMm;
    }

    public void setDistanceToWheel(int distanceToWheel) {
        this.distanceToWheel = distanceToWheel;
    }

    public void setConfigType(int configType) {
        this.configType = configType;
    }

    public void setInstallationPoint(int installationPoint) {
        this.installationPoint = installationPoint;
    }

    public void setStateNumber(String stateNumber) {
        this.stateNumber = stateNumber;
    }

    public int getFlagSystem() {
        return flagSystem;
    }

    public int getConfigSystem() {
        return configSystem;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public float getOffset() {
        return offset;
    }

    public int getBatteryMicrovoltsPerStep() {
        return batteryMicrovoltsPerStep;
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

    public int getConfigType() {
        return configType;
    }

    public int getInstallationPoint() {
        return installationPoint;
    }

    public String getStateNumber() {
        return stateNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return "SensorConfig{" +
                "flagSystem=" + flagSystem +
                ", configSystem=" + configSystem +
                ", multiplier=" + multiplier +
                ", offset=" + offset +
                ", batteryMicrovoltsPerStep=" + batteryMicrovoltsPerStep +
                ", messageDeliveryPeriod=" + messageDeliveryPeriod +
                ", measurementPeriod=" + measurementPeriod +
                ", distanceBetweenAxlesOneTwoMm=" + distanceBetweenAxlesOneTwoMm +
                ", distanceBetweenAxlesTwoThreeMm=" + distanceBetweenAxlesTwoThreeMm +
                ", distanceToWheel=" + distanceToWheel +
                ", configType=" + configType +
                ", installationPoint=" + installationPoint +
                ", stateNumber='" + stateNumber + '\'' +
                '}';
    }

    public static class Builder {
        private int flagSystem;
        private int configSystem;
        private float multiplier;
        private float offset;
        private int batteryMicrovoltsPerStep;
        private int messageDeliveryPeriod;
        private int measurementPeriod;
        private int distanceBetweenAxlesOneTwoMm;
        private int distanceBetweenAxlesTwoThreeMm;
        private int distanceToWheel;
        private int configType;
        private int installationPoint;
        private String stateNumber;

        public Builder setFlagSystem(int flagSystem) {
            this.flagSystem = flagSystem;
            return this;
        }

        public Builder setConfigSystem(int configSystem) {
            this.configSystem = configSystem;
            return this;
        }

        public Builder setMultiplier(float multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        public Builder setOffset(float offset) {
            this.offset = offset;
            return this;
        }

        public Builder setBatteryMicrovoltsPerStep(int batteryMicrovoltsPerStep) {
            this.batteryMicrovoltsPerStep = batteryMicrovoltsPerStep;
            return this;
        }

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

        public Builder setConfigType(int configType) {
            this.configType = configType;
            return this;
        }

        public Builder setInstallationPoint(int installationPoint) {
            this.installationPoint = installationPoint;
            return this;
        }

        public Builder setStateNumber(String stateNumber) {
            this.stateNumber = stateNumber;
            return this;
        }

        public SensorConfig build() {
            return new SensorConfig(this);
        }
    }
}
