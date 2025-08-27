package com.mehatronics.axle_load.data.mapper;

import com.mehatronics.axle_load.data.dto.ConfiguredDeviceDTO;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.AxisUiModel;
import com.mehatronics.axle_load.domain.entities.device.Device;

import java.util.List;

public interface ConfiguredDeviceMapper {

    ConfiguredDeviceDTO convertToConfiguredDevice(Device device);

    AxisUiModel toUiModel(AxisModel axis, List<ConfiguredDeviceDTO> devices);
}