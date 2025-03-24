package com.mehatronics.axle_load.di;

import com.mehatronics.axle_load.adapter.LoadingManager;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface LoadingManagerEntryPoint {
    LoadingManager getLoadingManager();
}