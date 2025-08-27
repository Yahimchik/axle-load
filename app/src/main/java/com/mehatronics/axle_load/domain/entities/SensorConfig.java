package com.mehatronics.axle_load.domain.entities;

import androidx.annotation.NonNull;

public class SensorConfig {
    private final String mac;
    private final int flagSystem;
    private final int configSystem;
    private final float multiplier;
    private final float offset;
    private final int batteryMicrovoltsPerStep;
    private int messageDeliveryPeriod;
    private int measurementPeriod;
    private final int distanceBetweenAxlesOneTwoMm;
    private final int distanceBetweenAxlesTwoThreeMm;
    private final int distanceToWheel;
    private final int configType;
    private final int installationPoint;
    private String stateNumber;
    private int carType;
    private int totalNumberOfAxleOnChassis;
    private int totalNumberOfSensorsOnChassis;
    private int numberOfAxle;
    private int installationPosition;
    private int totalNumberOfSensorsOnAxle;

    public SensorConfig(Builder builder) {
        this.mac = builder.mac;
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
        this.carType = builder.carType;
        this. totalNumberOfAxleOnChassis = builder.totalNumberOfAxleOnChassis;
        this. totalNumberOfSensorsOnChassis = builder.totalNumberOfSensorsOnChassis;
        this. numberOfAxle = builder.numberOfAxle;
        this. installationPosition = builder.installationPosition;
        this. totalNumberOfSensorsOnAxle = builder.totalNumberOfSensorsOnAxle;
    }

    public String getMac() {
        return mac;
    }


    public void setMessageDeliveryPeriod(int messageDeliveryPeriod) {
        this.messageDeliveryPeriod = messageDeliveryPeriod;
    }

    public void setMeasurementPeriod(int measurementPeriod) {
        this.measurementPeriod = measurementPeriod;
    }

    public void setStateNumber(String stateNumber) {
        this.stateNumber = stateNumber;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }

    public void setTotalNumberOfAxleOnChassis(int totalNumberOfAxleOnChassis) {
        this.totalNumberOfAxleOnChassis = totalNumberOfAxleOnChassis;
    }

    public void setTotalNumberOfSensorsOnChassis(int totalNumberOfSensorsOnChassis) {
        this.totalNumberOfSensorsOnChassis = totalNumberOfSensorsOnChassis;
    }

    public void setNumberOfAxle(int numberOfAxle) {
        this.numberOfAxle = numberOfAxle;
    }

    public void setInstallationPosition(int installationPosition) {
        this.installationPosition = installationPosition;
    }

    public void setTotalNumberOfSensorsOnAxle(int totalNumberOfSensorsOnAxle) {
        this.totalNumberOfSensorsOnAxle = totalNumberOfSensorsOnAxle;
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

    public String getStateNumber() {
        return stateNumber;
    }

    public int getCarType() {
        return carType;
    }

    public int getTotalNumberOfAxleOnChassis() {
        return totalNumberOfAxleOnChassis;
    }

    public int getTotalNumberOfSensorsOnChassis() {
        return totalNumberOfSensorsOnChassis;
    }

    public int getNumberOfAxle() {
        return numberOfAxle;
    }

    public int getInstallationPosition() {
        return installationPosition;
    }

    public int getTotalNumberOfSensorsOnAxle() {
        return totalNumberOfSensorsOnAxle;
    }

    @NonNull
    @Override
    public String toString() {
        return "SensorConfig{" +
                "mac='" + mac + '\'' +
                ", flagSystem=" + flagSystem +
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
                ", carType=" + carType +
                ", totalNumberOfAxleOnChassis=" + totalNumberOfAxleOnChassis +
                ", totalNumberOfSensorsOnChassis=" + totalNumberOfSensorsOnChassis +
                ", numberOfAxle=" + numberOfAxle +
                ", installationPosition=" + installationPosition +
                ", totalNumberOfSensorsOnAxle=" + totalNumberOfSensorsOnAxle +
                '}';
    }

    public static class Builder {
        private String mac;
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
        private int carType;
        private int totalNumberOfAxleOnChassis;
        private int totalNumberOfSensorsOnChassis;
        private int numberOfAxle;
        private int installationPosition;
        private int totalNumberOfSensorsOnAxle;

        public Builder setMac(String mac){
            this.mac = mac;
            return this;
        }

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

        public Builder carType(int carType){
            this.carType = carType;
            return this;
        }
        public Builder totalNumberOfAxleOnChassis(int totalNumberOfAxleOnChassis){
            this.totalNumberOfAxleOnChassis = totalNumberOfAxleOnChassis;
            return this;
        }
        public Builder totalNumberOfSensorsOnChassis(int totalNumberOfSensorsOnChassis){
            this.totalNumberOfSensorsOnChassis = totalNumberOfSensorsOnChassis;
            return this;
        }
        public Builder numberOfAxle(int numberOfAxle){
            this.numberOfAxle = numberOfAxle;
            return this;
        }

        public Builder installationPosition(int installationPosition){
            this.installationPosition = installationPosition;
            return this;
        }

        public Builder totalNumberOfSensorsOnAxle(int totalNumberOfSensorsOnAxle){
            this.totalNumberOfSensorsOnAxle = totalNumberOfSensorsOnAxle;
            return this;
        }

        public SensorConfig build() {
            return new SensorConfig(this);
        }
    }
}
