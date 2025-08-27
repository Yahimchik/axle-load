package com.mehatronics.axle_load.domain.entities;

import androidx.annotation.NonNull;

import com.mehatronics.axle_load.domain.entities.enums.AxisSide;

import java.util.Objects;

public record InstalationPoint(
        int axleNumber,
        AxisSide position
) {

    @NonNull
    @Override
    public String toString() {
        return "InstalationPoint{" +
                "axleNumber=" + axleNumber +
                ", position=" + position +
                '}';
    }

}