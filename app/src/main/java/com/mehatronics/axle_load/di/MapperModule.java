package com.mehatronics.axle_load.di;

import com.mehatronics.axle_load.data.format.DeviceDetailsFormatter;
import com.mehatronics.axle_load.data.format.SensorConfigFormatter;
import com.mehatronics.axle_load.data.format.impl.DeviceDetailsFormatterImpl;
import com.mehatronics.axle_load.data.format.impl.SensorConfigFormatterImpl;
import com.mehatronics.axle_load.data.mapper.ConfiguredDeviceMapper;
import com.mehatronics.axle_load.data.mapper.DateFormatMapper;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.data.mapper.GattDataMapper;
import com.mehatronics.axle_load.data.mapper.impl.ConfiguredDeviceMapperImpl;
import com.mehatronics.axle_load.data.mapper.impl.DateFormatMapperImpl;
import com.mehatronics.axle_load.data.mapper.impl.DeviceMapperImpl;
import com.mehatronics.axle_load.data.mapper.impl.GattDataMapperImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class MapperModule {
    @Binds
    @Singleton
    public abstract DeviceMapper bindDeviceMapper(DeviceMapperImpl impl);

    @Binds
    @Singleton
    public abstract GattDataMapper bindGattDataMapper(GattDataMapperImpl impl);

    @Binds
    @Singleton
    public abstract DateFormatMapper bindDateFormatMapper(DateFormatMapperImpl impl);

    @Binds
    @Singleton
    public abstract DeviceDetailsFormatter bindDeviceDetailsFormatter(DeviceDetailsFormatterImpl impl);

    @Binds
    @Singleton
    public abstract SensorConfigFormatter bindSensorConfigFormatter(SensorConfigFormatterImpl impl);

    @Binds
    @Singleton
    public abstract ConfiguredDeviceMapper bindConfiguredDeviceMapper(ConfiguredDeviceMapperImpl impl);
}