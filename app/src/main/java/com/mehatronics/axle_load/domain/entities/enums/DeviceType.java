package com.mehatronics.axle_load.domain.entities.enums;

import androidx.annotation.NonNull;

public enum DeviceType {
    DPS("DPS"),
    DSS("DSS"),
    DDS("DDS"),
    BT_COM_MINI("BT COM mini");

    private final String displayName;

    DeviceType(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}