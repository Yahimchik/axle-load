package com.mehatronics.axle_load.domain.entities;

import java.util.Objects;

public record AxisUiModel(
        String macLeft,
        String macRight,
        String macCenter,
        String weightLeft,
        String pressureLeft,
        String weightRight,
        String pressureRight,
        boolean isLeftConnected,
        boolean isRightConnected,
        boolean isCenterConnected
) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AxisUiModel that)) return false;
        return isLeftConnected == that.isLeftConnected
                && isRightConnected == that.isRightConnected
                && isCenterConnected == that.isCenterConnected
                && Objects.equals(macLeft, that.macLeft)
                && Objects.equals(macRight, that.macRight)
                && Objects.equals(macCenter, that.macCenter)
                && Objects.equals(weightLeft, that.weightLeft)
                && Objects.equals(pressureLeft, that.pressureLeft)
                && Objects.equals(weightRight, that.weightRight)
                && Objects.equals(pressureRight, that.pressureRight);
    }
}