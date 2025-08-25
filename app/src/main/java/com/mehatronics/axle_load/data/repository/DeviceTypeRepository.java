package com.mehatronics.axle_load.data.repository;

import com.mehatronics.axle_load.domain.entities.enums.ConnectStatus;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;

public interface DeviceTypeRepository {

    DeviceType getCurrDeviceType();

    void setDeviceType(DeviceType type);

    ConnectStatus getStatus();

    void setStatus(ConnectStatus status);
}