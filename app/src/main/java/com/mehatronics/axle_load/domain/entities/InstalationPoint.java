package com.mehatronics.axle_load.domain.entities;

import androidx.annotation.NonNull;

public class InstalationPoint {
    private final int axleNumber;
    private final int position;

    public InstalationPoint(Builder builder) {
        this.axleNumber = builder.axleNumber;
        this.position = builder.posotion;
    }

    @NonNull
    @Override
    public String toString() {
        return "InstalationPoint{" +
                "axleNumber=" + axleNumber +
                ", position=" + position +
                '}';
    }

    public static class Builder {
        private int axleNumber;
        private int posotion;

        public Builder setAxleNumber(int axleNumber) {
            this.axleNumber = axleNumber;
            return this;
        }

        public Builder setPosition(int posotion) {
            this.posotion = posotion;
            return this;
        }

        public InstalationPoint build() {
            return new InstalationPoint(this);
        }
    }
}
