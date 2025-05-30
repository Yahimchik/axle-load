package com.mehatronics.axle_load.domain.manager;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

public class SharedPreferencesManager {
    private final SharedPreferences prefs;

    @Inject
    public SharedPreferencesManager(Application application) {
        prefs = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public String get(String key, String def) {
        return prefs.getString(key, def);
    }

    public void put(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }
}
