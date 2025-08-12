package com.mehatronics.axle_load.data.repository.impl;

import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceTypeRepositoryImpl implements DeviceTypeRepository {

    private DeviceType currentDeviceType;

    @Inject
    public DeviceTypeRepositoryImpl() {
    }

    @Override
    public DeviceType getCurrDeviceType() {
        return currentDeviceType == null ? DeviceType.DPS : currentDeviceType;
    }

    @Override
    public void setDeviceType(DeviceType type) {
        this.currentDeviceType = type;
    }
}
