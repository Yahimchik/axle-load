package com.mehatronics.axle_load.domain.manager;

import static android.content.Context.MODE_PRIVATE;
import static com.mehatronics.axle_load.constants.BundleKeys.APP_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

public class SharedPreferencesManager {
    private final SharedPreferences prefs;

    @Inject
    public SharedPreferencesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
    }

    public String get(String key, String def) {
        return prefs.getString(key, def);
    }

    public void put(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }
}