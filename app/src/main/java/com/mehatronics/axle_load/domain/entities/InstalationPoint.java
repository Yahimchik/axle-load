package com.mehatronics.axle_load.domain.entities;

import androidx.annotation.NonNull;

public class InstalationPoint {
    private final int axleNumber;
    private final int position;

    public InstalationPoint(int axleNumber, int position) {
        this.axleNumber = axleNumber;
        this.position = position;
    }

    public InstalationPoint(Builder builder) {
        this.axleNumber = builder.axleNumber;
        this.position = builder.position;
    }

    @NonNull
    @Override
    public String toString() {
        return "InstalationPoint{" +
                "axleNumber=" + axleNumber +
                ", position=" + position +
                '}';
    }

    public int getAxleNumber() {
        return axleNumber;
    }

    public int getPosition() {
        return position;
    }

    public static class Builder {
        private int axleNumber;
        private int position;

        public Builder setAxleNumber(int axleNumber) {
            this.axleNumber = axleNumber;
            return this;
        }

        public Builder setPosition(int posotion) {
            this.position = posotion;
            return this;
        }

        public InstalationPoint build() {
            return new InstalationPoint(this);
        }
    }
}
