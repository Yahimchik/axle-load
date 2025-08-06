package com.mehatronics.axle_load.data.mapper.impl;

import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToValue;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import com.mehatronics.axle_load.data.dto.DeviceResponseDTO;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceMapperImpl implements DeviceMapper {

    @Inject
    public DeviceMapperImpl() {
    }
    @Override
    public DeviceResponseDTO convertToDeviceDTO(Device device) {
        BluetoothDevice bt = device.getDevice();
        ScanResult scan = device.getScanResult();

        if (bt == null || scan == null || scan.getScanRecord() == null) {
            return new DeviceResponseDTO("Unknown", "-", "-", "-", "-", device);
        }

        byte[] bytes = scan.getScanRecord().getBytes();

        try {
            String name = bt.getName() != null ? bt.getName() : "Unknown";
            String mac = bt.getAddress();
            String rssi = scan.getRssi() + " dBm";
            float weight = convertBytesToValue(bytes, 23, 24);
            float pressure = convertBytesToValue(bytes, 21, 22) / 10f;

            return new DeviceResponseDTO(name, mac, rssi, weight + " Kg", pressure + " kPa", device);

        } catch (SecurityException e) {
            return new DeviceResponseDTO("Security error", "-", "-", "-", "-", device);
        }
    }
}
