package com.mehatronics.axle_load.domain.entities;

import androidx.annotation.NonNull;

public record AxisModel(
        int number
) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AxisModel axisModel)) return false;
        return number == axisModel.number;
    }

    @NonNull
    @Override
    public String toString() {
        return "AxisModel{" +
                "number=" + number +
                '}';
    }
}
