package com.mehatronics.axle_load.entities;

import androidx.annotation.NonNull;

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
}
