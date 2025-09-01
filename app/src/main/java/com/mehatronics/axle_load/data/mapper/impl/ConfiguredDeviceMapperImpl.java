package com.mehatronics.axle_load.data.mapper.impl;

import static com.mehatronics.axle_load.constants.StringConstants.UNKNOWN;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToValue;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.dto.ConfiguredDeviceDTO;
import com.mehatronics.axle_load.data.mapper.ConfiguredDeviceMapper;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.AxisUiModel;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.localization.ResourceProvider;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConfiguredDeviceMapperImpl implements ConfiguredDeviceMapper {

    private final ResourceProvider provider;

    @Inject
    public ConfiguredDeviceMapperImpl(ResourceProvider provider) {
        this.provider = provider;
    }

    @Override
    public ConfiguredDeviceDTO convertToConfiguredDevice(Device device) {
        BluetoothDevice bt = device.getDevice();
        ScanResult scan = device.getScanResult();

        if (bt == null || scan == null || scan.getScanRecord() == null) {
            return new ConfiguredDeviceDTO(UNKNOWN, "-", "-");
        }

        byte[] bytes = scan.getScanRecord().getBytes();
        String mac = bt.getAddress();

        float weight = convertBytesToValue(bytes, 23, 24);
        float pressure = convertBytesToValue(bytes, 21, 22) / 10f;

        return new ConfiguredDeviceDTO(mac, weight + " Kg", pressure + " kPa");
    }

    @Override
    public AxisUiModel toUiModel(AxisModel axis, List<ConfiguredDeviceDTO> devices) {
        String title = provider.getString(R.string.axle_numbered, axis.getNumber());

        String leftMac = axis.getSideDeviceMap().get(AxisSide.LEFT);
        String rightMac = axis.getSideDeviceMap().get(AxisSide.RIGHT);
        String centerMac = axis.getSideDeviceMap().get(AxisSide.CENTER);

        ConfiguredDeviceDTO left = findByMac(devices, leftMac);
        ConfiguredDeviceDTO right = findByMac(devices, rightMac);
        ConfiguredDeviceDTO center = findByMac(devices, centerMac);

        return new AxisUiModel(
                title, leftMac, rightMac, centerMac,
                left != null ? left.weight() : "", left != null ? left.pressure() : "",
                center != null ? center.weight() : right != null ? right.weight() : "",
                center != null ? center.pressure() : right != null ? right.pressure() : "",
                left != null, right != null, center != null
        );
    }

    private ConfiguredDeviceDTO findByMac(List<ConfiguredDeviceDTO> devices, String mac) {
        if (mac == null) return null;
        for (ConfiguredDeviceDTO dto : devices) {
            if (dto.mac().equals(mac)) return dto;
        }
        return null;
    }
}