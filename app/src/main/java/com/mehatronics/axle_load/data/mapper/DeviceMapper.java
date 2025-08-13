package com.mehatronics.axle_load.data.mapper;

import com.mehatronics.axle_load.data.dto.DeviceResponseDTO;
import com.mehatronics.axle_load.domain.entities.device.Device;

public interface DeviceMapper {
    DeviceResponseDTO convertToDeviceDTO(Device device);
}
