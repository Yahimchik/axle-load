package com.mehatronics.axle_load.domain.entities;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

public class CalibrationTable {
    private UUID id;
    private  int detector;
    private  float multiplier;
    private final boolean isLast;

    public CalibrationTable(int detector, float multiplier) {
        this(UUID.randomUUID(), detector, multiplier, false);
    }

    public CalibrationTable(int detector, float multiplier, boolean isLast) {
        this(UUID.randomUUID(), detector, multiplier, isLast);
    }

    public CalibrationTable(UUID id, int detector, float multiplier, boolean isLast) {
        this.id = id;
        this.detector = detector;
        this.multiplier = multiplier;
        this.isLast = isLast;
    }

    public boolean isLast() {
        return isLast;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public int getDetector() {
        return detector;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setDetector(int detector) {
        this.detector = detector;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    @NonNull
    @Override
    public String toString() {
        return "CalibrationTable{" +
                "id=" + id +
                ", detector=" + detector +
                ", multiplier=" + multiplier +
                ", isLast=" + isLast +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalibrationTable that = (CalibrationTable) o;
        return /*id.equals(that.id) &&*/
                detector == that.detector &&
                Float.compare(that.multiplier, multiplier) == 0 &&
                isLast == that.isLast;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, detector, multiplier);
    }
}
