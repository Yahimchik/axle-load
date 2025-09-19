package com.mehatronics.axle_load.data.mapper.impl;

import static com.mehatronics.axle_load.R.string.axle_center;
import static com.mehatronics.axle_load.R.string.axle_left;
import static com.mehatronics.axle_load.R.string.axle_right;
import static com.mehatronics.axle_load.R.string.search;
import static com.mehatronics.axle_load.constants.StringConstants.UNKNOWN;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.DPS;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToValue;
import static com.mehatronics.axle_load.utils.ByteUtils.extractStringFromBytes;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import androidx.annotation.RequiresPermission;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.dto.DeviceResponseDTO;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.localization.ResourceProvider;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceMapperImpl implements DeviceMapper {

    private final Map<String, byte[]> deviceBuffer = new ConcurrentHashMap<>();
    private final DeviceTypeRepository repository;
    private final ResourceProvider provider;

    @Inject
    public DeviceMapperImpl(DeviceTypeRepository repository, ResourceProvider provider) {
        this.repository = repository;
        this.provider = provider;
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public DeviceResponseDTO convertToDeviceDTO(Device device) {
        BluetoothDevice bt = device.getDevice();
        ScanResult scan = device.getScanResult();

        if (bt == null || scan == null || scan.getScanRecord() == null) {
            return new DeviceResponseDTO();
        }

        byte[] bytes = scan.getScanRecord().getBytes();
        String name = bt.getName() != null ? bt.getName() : UNKNOWN;
        String mac = bt.getAddress();

        String rssi = scan.getRssi() + " dBm";
        float weight = convertBytesToValue(bytes, 23, 24);
        float pressure = convertBytesToValue(bytes, 21, 22) / 10f;
        int battery = bytes[30];

        String stateNumber = provider.getString(search);
        String installPlace = provider.getString(search);

        if (repository.getCurrDeviceType().equals(BT_COM_MINI)) {
            stateNumber = extractStringFromBytes(bytes, 33, 10);
        }

        if (repository.getCurrDeviceType().equals(DPS)) {
            int partIndex = bytes[26] & 0xFF;
            byte v1 = bytes[27];
            byte v2 = bytes[28];

            byte[] buffer = deviceBuffer.computeIfAbsent(mac, k -> new byte[18]);

            buffer[partIndex * 2] = v1;
            buffer[partIndex * 2 + 1] = v2;

            boolean hasStateNumber = isHasStateNumber(buffer);
            boolean hasInstallPlace = buffer[17] != 0;

            if (hasStateNumber && hasInstallPlace) {
                installPlace = parseSensorNumber((buffer[17] & 0xFF));
                stateNumber = new String(Arrays.copyOfRange(buffer, 0, 10)).trim();
            }
        }

        return new DeviceResponseDTO(name, mac, rssi, weight + " Kg", pressure + " kPa", device, battery + " %", stateNumber, installPlace);
    }

    private boolean isHasStateNumber(byte[] buffer) {
        for (int i = 0; i < 10; i++) {
            if (buffer[i] == 0) {
                return false;
            }
        }
        return true;
    }

    public String parseSensorNumber(int number) {
        int axleNumber = (number & 0b00000111);
        int point = ((number >> 3) & 0b00000011);

        String pos = switch (point) {
            case 0 -> provider.getString(axle_center);
            case 1 -> provider.getString(axle_left);
            case 2 -> provider.getString(axle_right);
            default -> UNKNOWN;
        };
        return provider.getString(R.string.axle_numbered, axleNumber) + " — " + pos;
    }
}