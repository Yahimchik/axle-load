package com.mehatronics.axle_load.domain.entities.device;

public class DeviceInfoToSave {
    private int password = 180825;
    private int type;
    private String carNumberFirst;
    private String carNumberSecond;
    public DeviceInfoToSave() {
    }

    public int getPassword() {
        return password;
    }

    public int getType() {
        return type;
    }

    public String getCarNumberFirst() {
        return carNumberFirst;
    }

    public String getCarNumberSecond() {
        return carNumberSecond;
    }

    private DeviceInfoToSave(Builder builder) {
        this.password = builder.password;
        this.type = builder.type;
        this.carNumberFirst = builder.carNumberFirst;
        this.carNumberSecond = builder.carNumberSecond;
    }

    public static class Builder {
        private int password = 180825;
        private int type;
        private String carNumberFirst;
        private String carNumberSecond;

        public Builder password(int password) {
            this.password = password;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder carNumberFirst(String carNumberFirst) {
            this.carNumberFirst = carNumberFirst;
            return this;
        }

        public Builder carNumberSecond(String carNumberSecond) {
            this.carNumberSecond = carNumberSecond;
            return this;
        }

        public DeviceInfoToSave build() {
            return new DeviceInfoToSave(this);
        }
    }
}
