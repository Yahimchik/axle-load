package com.mehatronics.axle_load.configuration;

import android.app.Activity;

import com.mehatronics.axle_load.activity.DDSActivity;
import com.mehatronics.axle_load.activity.DPSActivity;
import com.mehatronics.axle_load.activity.DSSActivity;
import com.mehatronics.axle_load.navigation.ActivityNavigator;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@InstallIn(ActivityComponent.class)
@Module
public class ActivityModule {

    @Provides
    public Class<? extends Activity>[] provideActivities() {
        return new Class[]{DPSActivity.class, DSSActivity.class, DDSActivity.class};
    }

    @Provides
    public ActivityNavigator provideActivityNavigator(Class<? extends Activity>[] activities) {
        return new ActivityNavigator(activities);
    }
}


