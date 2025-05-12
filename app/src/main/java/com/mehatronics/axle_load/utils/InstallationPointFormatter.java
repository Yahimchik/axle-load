package com.mehatronics.axle_load.utils;

import static com.mehatronics.axle_load.utils.constants.StringConstants.AXLE;
import static com.mehatronics.axle_load.utils.constants.StringConstants.LEFT;
import static com.mehatronics.axle_load.utils.constants.StringConstants.RIGHT;

import java.util.ArrayList;
import java.util.List;

public class InstallationPointFormatter {
    public static String format(int installationPoint) {
        int axle = (installationPoint - 1) / 2 + 1;
        String position = (installationPoint - 1) % 2 == 0 ? LEFT : RIGHT;
        return AXLE + " " + axle + " — " + position;
    }

    public static List<String> getFormattedList(int count) {
        List<String> points = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            points.add(format(i));
        }
        return points;
    }

    public static int fromFormatted(String formatted) {
        return getFormattedList(6).indexOf(formatted) + 1;
    }
}
