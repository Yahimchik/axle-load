package com.mehatronics.axle_load.di;

import com.mehatronics.axle_load.adapter.CalibrationTableAdapter;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;

@Module
@InstallIn(FragmentComponent.class)
public class AdapterModule {

    @Provides
    public CalibrationTableAdapter provideCalibrationTableAdapter() {
        return new CalibrationTableAdapter();
    }
}