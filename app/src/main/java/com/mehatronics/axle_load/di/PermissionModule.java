package com.mehatronics.axle_load.di;

import android.content.Context;

import com.mehatronics.axle_load.security.permissions.service.impl.PermissionServiceImpl;
import com.mehatronics.axle_load.security.permissions.usecase.PermissionUseCase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public  class PermissionModule {

    @Provides
    @Singleton
    public static PermissionServiceImpl providePermissionManager(@ApplicationContext Context context) {
        return new PermissionServiceImpl(context);
    }

    @Provides
    @Singleton
    public static PermissionUseCase providePermissionUseCase(PermissionServiceImpl permissionServiceImpl) {
        return new PermissionUseCase(permissionServiceImpl);
    }
}

