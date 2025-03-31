package com.mehatronics.axle_load.entities;

public class CalibrationElementWeightMulti {

    private int detector;
    private int multiplier;

    public CalibrationElementWeightMulti() {
    }

    public CalibrationElementWeightMulti(int detector, int multiplier) {
        this.detector = detector;
        this.multiplier = multiplier;
    }

    public int getDetector() {
        return detector;
    }

    public void setDetector(int detector) {
        this.detector = detector;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}
