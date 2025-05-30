package com.mehatronics.axle_load.mapper;

import com.mehatronics.axle_load.dto.DeviceResponseDTO;
import com.mehatronics.axle_load.entities.Device;

public interface DeviceMapper {
    DeviceResponseDTO convertToDeviceDTO(Device device);
}
