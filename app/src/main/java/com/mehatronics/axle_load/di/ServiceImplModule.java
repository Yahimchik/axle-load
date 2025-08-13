package com.mehatronics.axle_load.di;

import android.content.Context;

import com.mehatronics.axle_load.data.repository.DeviceRepository;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.data.repository.impl.DeviceRepositoryImpl;
import com.mehatronics.axle_load.data.repository.impl.DeviceTypeRepositoryImpl;
import com.mehatronics.axle_load.data.repository.impl.PasswordRepositoryImpl;
import com.mehatronics.axle_load.data.service.AxisService;
import com.mehatronics.axle_load.data.service.BleScannerService;
import com.mehatronics.axle_load.data.service.GattReadService;
import com.mehatronics.axle_load.data.service.GattWriteService;
import com.mehatronics.axle_load.data.service.PermissionHandlerService;
import com.mehatronics.axle_load.data.service.PermissionObserverService;
import com.mehatronics.axle_load.data.service.PermissionService;
import com.mehatronics.axle_load.data.service.SaveToFileService;
import com.mehatronics.axle_load.data.service.SensorSelectionService;
import com.mehatronics.axle_load.data.service.SensorService;
import com.mehatronics.axle_load.data.service.impl.AxisServiceImpl;
import com.mehatronics.axle_load.data.service.impl.BleScannerServiceImpl;
import com.mehatronics.axle_load.data.service.impl.GattReadServiceImpl;
import com.mehatronics.axle_load.data.service.impl.GattWriteServiceImpl;
import com.mehatronics.axle_load.data.service.impl.PermissionHandlerServiceImpl;
import com.mehatronics.axle_load.data.service.impl.PermissionObserverServiceImpl;
import com.mehatronics.axle_load.data.service.impl.PermissionServiceImpl;
import com.mehatronics.axle_load.data.service.impl.SaveToFileServiceImpl;
import com.mehatronics.axle_load.data.service.impl.SensorSelectionServiceImpl;
import com.mehatronics.axle_load.data.service.impl.SensorServiceImpl;
import com.mehatronics.axle_load.domain.usecase.ChangeLanguageUseCase;
import com.mehatronics.axle_load.domain.usecase.PermissionUseCase;
import com.mehatronics.axle_load.domain.usecase.SaveCalibrationTableUseCase;
import com.mehatronics.axle_load.domain.usecase.SubmitPasswordUseCase;
import com.mehatronics.axle_load.domain.usecase.ValidateAxisCountUseCase;
import com.mehatronics.axle_load.domain.usecase.impl.ChangeLanguageUseCaseImpl;
import com.mehatronics.axle_load.domain.usecase.impl.PermissionUseCaseImpl;
import com.mehatronics.axle_load.domain.usecase.impl.SaveCalibrationTableUseCaseImpl;
import com.mehatronics.axle_load.domain.usecase.impl.SubmitPasswordUseCaseImpl;
import com.mehatronics.axle_load.domain.usecase.impl.ValidateAxisCountUseCaseImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ServiceImplModule {
    @Provides
    @Singleton
    public static PermissionServiceImpl providePermissionManager(@ApplicationContext Context context) {
        return new PermissionServiceImpl(context);
    }

    @Binds
    @Singleton
    public abstract PermissionService bindPermissionService(PermissionServiceImpl impl);

    @Binds
    @Singleton
    public abstract PermissionUseCase bindPermissionUseCase(PermissionUseCaseImpl impl);

    @Binds
    @Singleton
    public abstract PermissionHandlerService bindPermissionHandler(PermissionHandlerServiceImpl impl);

    @Binds
    @Singleton
    public abstract ChangeLanguageUseCase bindChangeLanguageUseCase(ChangeLanguageUseCaseImpl impl);

    @Binds
    @Singleton
    public abstract SaveCalibrationTableUseCase bindSaveCalibrationTableUseCase(SaveCalibrationTableUseCaseImpl impl);

    @Binds
    @Singleton
    public abstract ValidateAxisCountUseCase bindValidateAxisCountUseCase(ValidateAxisCountUseCaseImpl impl);

    @Binds
    @Singleton
    public abstract GattWriteService bindGattWriteService(GattWriteServiceImpl impl);

    @Binds
    @Singleton
    public abstract GattReadService bindGattReadService(GattReadServiceImpl impl);

    @Binds
    @Singleton
    public abstract BleScannerService bindBleScannerService(BleScannerServiceImpl impl);

    @Binds
    @Singleton
    public abstract PermissionObserverService bindPermissionObserverService(PermissionObserverServiceImpl impl);

    @Binds
    @Singleton
    public abstract AxisService bindAxisService(AxisServiceImpl impl);

    @Binds
    @Singleton
    public abstract SensorService bindSensorService(SensorServiceImpl impl);

    @Binds
    @Singleton
    public abstract SensorSelectionService bindSensorSelectionManager(SensorSelectionServiceImpl impl);

    @Binds
    @Singleton
    public abstract PasswordRepository bindPasswordRepository(PasswordRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract SubmitPasswordUseCase bindSubmitPasswordUseCase(SubmitPasswordUseCaseImpl impl);

    @Binds
    @Singleton
    public abstract DeviceRepository bindDeviceRepository(DeviceRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract SaveToFileService bindSaveToFileService(SaveToFileServiceImpl impl);

    @Binds
    @Singleton
    public abstract DeviceTypeRepository bindDeviceTypeRepository(DeviceTypeRepositoryImpl impl);
}