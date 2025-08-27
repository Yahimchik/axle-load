package com.mehatronics.axle_load.domain.entities.device;

import androidx.annotation.NonNull;

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

    public void setPassword(int password) {
        this.password = password;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCarNumberFirst(String carNumberFirst) {
        this.carNumberFirst = carNumberFirst;
    }

    public void setCarNumberSecond(String carNumberSecond) {
        this.carNumberSecond = carNumberSecond;
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceInfoToSave{" +
                "password=" + password +
                ", type=" + type +
                ", carNumberFirst='" + carNumberFirst + '\'' +
                ", carNumberSecond='" + carNumberSecond + '\'' +
                '}';
    }
}