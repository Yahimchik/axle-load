package com.mehatronics.axle_load.entities;

import androidx.annotation.NonNull;

import java.util.Objects;

public class CalibrationTable {

    private int detector;
    private float multiplier;

    public CalibrationTable(int detector, float multiplier) {
        this.detector = detector;
        this.multiplier = multiplier;
    }

    public int getDetector() {
        return detector;
    }

    public void setDetector(int detector) {
        this.detector = detector;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    @NonNull
    @Override
    public String toString() {
        return "CalibrationElementWeightMulti{" +
                "detector=" + detector +
                ", multiplier=" + multiplier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalibrationTable)) return false;
        CalibrationTable that = (CalibrationTable) o;
        return detector == that.detector &&
                Float.compare(that.multiplier, multiplier) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(detector, multiplier);
    }
}
