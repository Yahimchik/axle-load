package com.mehatronics.axle_load.entities;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class DeviceDetails implements Serializable {
    private final String deviceName;
    private final String dateManufacturer;
    private final String manufacturer;
    private final String modelType;
    private final String serialNumber;
    private final String firmwareVersion;
    private final String hardWareVersion;
    private final String batteryLevel;
    private final String weight;
    private final String pressure;

    private DeviceDetails(Builder builder) {
        this.deviceName = builder.deviceName;
        this.dateManufacturer = builder.dateManufacturer;
        this.manufacturer = builder.manufacturer;
        this.modelType = builder.modelType;
        this.serialNumber = builder.serialNumber;
        this.firmwareVersion = builder.firmwareVersion;
        this.hardWareVersion = builder.hardWareVersion;
        this.batteryLevel = builder.batteryLevel;
        this.weight = builder.weight;
        this.pressure = builder.pressure;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDateManufacturer() {
        return dateManufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModelType() {
        return modelType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getHardWareVersion() {
        return hardWareVersion;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public String getWeight() {
        return weight;
    }

    public String getPressure() {
        return pressure;
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceDetails{" +
                "deviceName='" + deviceName + '\'' +
                ", dateManufacturer='" + dateManufacturer + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", modelType='" + modelType + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", hardWareVersion='" + hardWareVersion + '\'' +
                ", batteryLevel='" + batteryLevel + '\'' +
                ", weight='" + weight + '\'' +
                ", pressure='" + pressure + '\'' +
                '}';
    }

    public static class Builder {
        private String deviceName;
        private String dateManufacturer;
        private String manufacturer;
        private String modelType;
        private String serialNumber;
        private String firmwareVersion;
        private String hardWareVersion;
        private String batteryLevel;
        private String weight;
        private String pressure;

        public Builder setDeviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder setDateManufacturer(String dateManufacturer) {
            this.dateManufacturer = dateManufacturer;
            return this;
        }

        public Builder setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder setModelType(String modelType) {
            this.modelType = modelType;
            return this;
        }

        public Builder setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        public Builder setHardWareVersion(String hardWareVersion) {
            this.hardWareVersion = hardWareVersion;
            return this;
        }

        public Builder setBatteryLevel(String batteryLevel) {
            this.batteryLevel = batteryLevel;
            return this;
        }

        public Builder setWeight(String weight) {
            this.weight = weight;
            return this;
        }

        public Builder setPressure(String pressure) {
            this.pressure = pressure;
            return this;
        }

        public DeviceDetails build() {
            return new DeviceDetails(this);
        }
    }
}
