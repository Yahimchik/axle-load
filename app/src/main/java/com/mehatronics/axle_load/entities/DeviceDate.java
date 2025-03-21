package com.mehatronics.axle_load.entities;

public class DeviceDate {
    private final int year;
    private final int month;
    private final int day;

    public DeviceDate(Builder builder) {
        this.year = builder.year;
        this.month = builder.month;
        this.day = builder.day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public static class Builder {
        private int year;
        private int month;
        private int day;

        public Builder addYear(int year) {
            this.year = year;
            return this;
        }

        public Builder addMonth(int month) {
            this.month = month;
            return this;
        }

        public Builder addDay(int day) {
            this.day = day;
            return this;
        }

        public DeviceDate build() {
            return new DeviceDate(this);
        }
    }
}
