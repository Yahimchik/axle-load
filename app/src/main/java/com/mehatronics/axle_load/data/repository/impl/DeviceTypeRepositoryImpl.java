package com.mehatronics.axle_load.data.repository.impl;

import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.WAITING;

import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.domain.entities.enums.ConnectStatus;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceTypeRepositoryImpl implements DeviceTypeRepository {

    private DeviceType currentDeviceType;
    private ConnectStatus status;

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

    @Override
    public ConnectStatus getStatus() {
        return status == null ? WAITING : status;
    }

    @Override
    public void setStatus(ConnectStatus status) {
        this.status = status;
    }
}