package com.mehatronics.axle_load.domain.entities;

import androidx.annotation.NonNull;

import com.mehatronics.axle_load.domain.entities.enums.AxisSide;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class AxisModel {
    private final int number;
    private final Map<AxisSide, String> sideDeviceMap = new LinkedHashMap<>();

    public AxisModel(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setDeviceForSide(AxisSide side, String mac) {
        sideDeviceMap.put(side, mac);
    }

    public String getDeviceForSide(AxisSide side) {
        return sideDeviceMap.get(side);
    }

    public Map<AxisSide, String> getSideDeviceMap() {
        return sideDeviceMap;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AxisModel axisModel)) return false;
        return number == axisModel.number
                && Objects.equals(sideDeviceMap, axisModel.sideDeviceMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, sideDeviceMap);
    }

    @NonNull
    @Override
    public String toString() {
        return "AxisModel{" +
                "number=" + number +
                ", sideDeviceMap=" + sideDeviceMap +
                '}';
    }
}