package com.mehatronics.axle_load;

import android.app.Application;
import android.content.Context;

import com.mehatronics.axle_load.helper.LocaleHelper;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.attachBaseContext(base));
    }
}