package com.mehatronics.axle_load.configuration;

import android.content.Context;

import com.mehatronics.axle_load.permissions.service.PermissionService;
import com.mehatronics.axle_load.permissions.service.impl.PermissionServiceImpl;
import com.mehatronics.axle_load.permissions.usecase.PermissionUseCase;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class PermissionModule {

    @Provides
    @Singleton
    public static PermissionServiceImpl providePermissionManager(@ApplicationContext Context context) {
        return new PermissionServiceImpl(context);
    }

    @Binds
    abstract PermissionService bindPermissionService(PermissionServiceImpl impl);

    @Provides
    @Singleton
    public static PermissionUseCase providePermissionUseCase(PermissionServiceImpl permissionServiceImpl) {
        return new PermissionUseCase(permissionServiceImpl);
    }
}

