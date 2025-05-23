package com.mehatronics.axle_load.utils;

public class CalibrationParseResult {
    public final int nextPage;
    public final boolean tableCompleted;

    public CalibrationParseResult(int nextPage, boolean tableCompleted) {
        this.nextPage = nextPage;
        this.tableCompleted = tableCompleted;
    }
}
