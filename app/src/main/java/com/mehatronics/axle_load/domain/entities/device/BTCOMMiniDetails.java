package com.mehatronics.axle_load.domain.entities.device;

import java.util.Objects;

public class BTCOMMiniDetails {
    private String deviceName;
    private int serialType;
    private String serialNumber;
    private String firmwareVersion;
    private String hardwareVersion;
    private String dateManufacture;
    private int serialNum;
    private int numberOfRecords;
    private int configLoad;
    private int systemConfig;
    private int speedOmniModbusCAN;
    private int minValueOmni;
    private int maxValueOmni;
    private int omniConfig;
    private int omniInterval;
    private int btComMiniAddress;

    public BTCOMMiniDetails() {
    }

    private BTCOMMiniDetails(Builder builder) {
        this.deviceName = builder.deviceName;
        this.serialType = builder.serialType;
        this.serialNumber = builder.serialNumber;
        this.firmwareVersion = builder.firmwareVersion;
        this.hardwareVersion = builder.hardwareVersion;
        this.dateManufacture = builder.dateManufacture;
        this.serialNum = builder.serialNum;
        this.numberOfRecords = builder.numberOfRecords;
        this.configLoad = builder.configLoad;
        this.systemConfig = builder.systemConfig;
        this.speedOmniModbusCAN = builder.speedOmniModbusCAN;
        this.minValueOmni = builder.minValueOmni;
        this.maxValueOmni = builder.maxValueOmni;
        this.omniConfig = builder.omniConfig;
        this.omniInterval = builder.omniInterval;
        this.btComMiniAddress = builder.btComMiniAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getSerialType() {
        return serialType;
    }

    public void setSerialType(int serialType) {
        this.serialType = serialType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getDateManufacture() {
        return dateManufacture;
    }

    public void setDateManufacture(String dateManufacture) {
        this.dateManufacture = dateManufacture;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public int getConfigLoad() {
        return configLoad;
    }

    public void setConfigLoad(int configLoad) {
        this.configLoad = configLoad;
    }

    public int getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(int systemConfig) {
        this.systemConfig = systemConfig;
    }

    public int getSpeedOmniModbusCAN() {
        return speedOmniModbusCAN;
    }

    public void setSpeedOmniModbusCAN(int speedOmniModbusCAN) {
        this.speedOmniModbusCAN = speedOmniModbusCAN;
    }

    public int getMinValueOmni() {
        return minValueOmni;
    }

    public void setMinValueOmni(int minValueOmni) {
        this.minValueOmni = minValueOmni;
    }

    public int getMaxValueOmni() {
        return maxValueOmni;
    }

    public void setMaxValueOmni(int maxValueOmni) {
        this.maxValueOmni = maxValueOmni;
    }

    public int getOmniConfig() {
        return omniConfig;
    }

    public void setOmniConfig(int omniConfig) {
        this.omniConfig = omniConfig;
    }

    public int getOmniInterval() {
        return omniInterval;
    }

    public void setOmniInterval(int omniInterval) {
        this.omniInterval = omniInterval;
    }

    public int getBtComMiniAddress() {
        return btComMiniAddress;
    }

    public void setBtComMiniAddress(int btComMiniAddress) {
        this.btComMiniAddress = btComMiniAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BTCOMMiniDetails)) return false;
        BTCOMMiniDetails that = (BTCOMMiniDetails) o;
        return serialType == that.serialType
                && serialNum == that.serialNum
                && numberOfRecords == that.numberOfRecords
                && configLoad == that.configLoad
                && systemConfig == that.systemConfig
                && speedOmniModbusCAN == that.speedOmniModbusCAN
                && minValueOmni == that.minValueOmni
                && maxValueOmni == that.maxValueOmni
                && omniConfig == that.omniConfig
                && omniInterval == that.omniInterval
                && btComMiniAddress == that.btComMiniAddress
                && Objects.equals(serialNumber, that.serialNumber)
                && Objects.equals(firmwareVersion, that.firmwareVersion)
                && Objects.equals(hardwareVersion, that.hardwareVersion)
                && Objects.equals(dateManufacture, that.dateManufacture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialType,
                serialNumber,
                firmwareVersion,
                hardwareVersion,
                dateManufacture,
                serialNum,
                numberOfRecords,
                configLoad,
                systemConfig,
                speedOmniModbusCAN,
                minValueOmni,
                maxValueOmni,
                omniConfig,
                omniInterval,
                btComMiniAddress);
    }

    public static class Builder {
        private String deviceName;
        private int serialType;
        private String serialNumber;
        private String firmwareVersion;
        private String hardwareVersion;
        private String dateManufacture;
        private int serialNum;
        private int numberOfRecords;
        private int configLoad;
        private int systemConfig;
        private int speedOmniModbusCAN;
        private int minValueOmni;
        private int maxValueOmni;
        private int omniConfig;
        private int omniInterval;
        private int btComMiniAddress;

        public Builder deviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder serialType(int serialType) {
            this.serialType = serialType;
            return this;
        }

        public Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder firmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }

        public Builder hardwareVersion(String hardwareVersion) {
            this.hardwareVersion = hardwareVersion;
            return this;
        }

        public Builder dateManufacture(String dateManufacture) {
            this.dateManufacture = dateManufacture;
            return this;
        }

        public Builder serialNum(int serialNum) {
            this.serialNum = serialNum;
            return this;
        }

        public Builder numberOfRecords(int numberOfRecords) {
            this.numberOfRecords = numberOfRecords;
            return this;
        }

        public Builder configLoad(int configLoad) {
            this.configLoad = configLoad;
            return this;
        }

        public Builder systemConfig(int systemConfig) {
            this.systemConfig = systemConfig;
            return this;
        }

        public Builder speedOmniModbusCAN(int speedOmniModbusCAN) {
            this.speedOmniModbusCAN = speedOmniModbusCAN;
            return this;
        }

        public Builder minValueOmni(int minValueOmni) {
            this.minValueOmni = minValueOmni;
            return this;
        }

        public Builder maxValueOmni(int maxValueOmni) {
            this.maxValueOmni = maxValueOmni;
            return this;
        }

        public Builder omniConfig(int omniConfig) {
            this.omniConfig = omniConfig;
            return this;
        }

        public Builder omniInterval(int omniInterval) {
            this.omniInterval = omniInterval;
            return this;
        }

        public Builder btComMiniAddress(int btComMiniAddress) {
            this.btComMiniAddress = btComMiniAddress;
            return this;
        }

        public BTCOMMiniDetails build() {
            return new BTCOMMiniDetails(this);
        }
    }
}