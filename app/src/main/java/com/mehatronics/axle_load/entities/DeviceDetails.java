package com.mehatronics.axle_load.entities;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
    private List<CalibrationTable> table;
    private final SensorConfig sensorConfig;

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
        this.table = builder.table;
        this.sensorConfig = builder.sensorConfig;
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

    public List<CalibrationTable> getTable() {
        return table;
    }

    public void setTable(List<CalibrationTable> table) {
        this.table = table;
    }

    public SensorConfig getSensorConfig() {
        return sensorConfig;
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
                ", table=" + table +
                ", sensorConfig=" + sensorConfig +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DeviceDetails)) return false;
        DeviceDetails details = (DeviceDetails) o;
        return Objects.equals(deviceName, details.deviceName)
                && Objects.equals(dateManufacturer, details.dateManufacturer)
                && Objects.equals(manufacturer, details.manufacturer)
                && Objects.equals(modelType, details.modelType)
                && Objects.equals(serialNumber, details.serialNumber)
                && Objects.equals(firmwareVersion, details.firmwareVersion)
                && Objects.equals(hardWareVersion, details.hardWareVersion)
                && Objects.equals(batteryLevel, details.batteryLevel)
                && Objects.equals(weight, details.weight)
                && Objects.equals(pressure, details.pressure)
                && Objects.equals(table, details.table)
                && Objects.equals(sensorConfig, details.sensorConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                deviceName,
                dateManufacturer,
                manufacturer,
                modelType,
                serialNumber,
                firmwareVersion,
                hardWareVersion,
                batteryLevel,
                weight,
                pressure,
                table,
                sensorConfig);
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
        private List<CalibrationTable> table;
        private SensorConfig sensorConfig;

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

        public Builder setTable(List<CalibrationTable> table) {
            this.table = table;
            return this;
        }

        public Builder setSensorConfig(SensorConfig sensorConfig) {
            this.sensorConfig = sensorConfig;
            return this;
        }

        public DeviceDetails build() {
            return new DeviceDetails(this);
        }
    }
}
