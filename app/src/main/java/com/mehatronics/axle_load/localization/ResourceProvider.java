package com.mehatronics.axle_load.localization;

public interface ResourceProvider {
    String getString(int resId);
    String getString(int resId, Object... formatArgs);
}
