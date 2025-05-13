package com.mehatronics.axle_load.di;

import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.format.DeviceDetailsFormatter;
import com.mehatronics.axle_load.format.SensorConfigFormatter;
import com.mehatronics.axle_load.format.impl.DeviceDetailsFormatterImpl;
import com.mehatronics.axle_load.format.impl.SensorConfigFormatterImpl;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;

@Module
@InstallIn(FragmentComponent.class)
public class FormatterModule {

    @Provides
    public static DeviceDetailsFormatter<DeviceDetails> provideDeviceDetailsFormatter() {
        return new DeviceDetailsFormatterImpl();
    }

    @Provides
    public static SensorConfigFormatter<SensorConfig> provideSensorConfigFormatter() {
        return new SensorConfigFormatterImpl();
    }
}
