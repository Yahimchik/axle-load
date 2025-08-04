package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.mehatronics.axle_load.data.service.GattWriteService;
import com.mehatronics.axle_load.domain.strategy.CommandStrategy;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

public class GattWriteServiceImpl implements GattWriteService {

    private final Map<String, CommandStrategy> commandStrategies;
    private final byte[] buffer = new byte[68];

    @Inject
    public GattWriteServiceImpl(Map<String, CommandStrategy> commandStrategies) {
        this.commandStrategies = commandStrategies;
    }

    public void setCommand(int c1, int c2) {
        buffer[0] = (byte) c1;
        buffer[1] = (byte) c2;
        buffer[2] = 0;
        buffer[3] = 0;

        CommandStrategy strategy = commandStrategies.get(key(c1, c2));
        if (strategy != null) {
            strategy.fillBuffer(buffer);
        }
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void clearBuffer() {
        Arrays.fill(buffer, (byte) 0);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void write(BluetoothGatt gatt) {
        var service = gatt.getService(USER_SERVICE_DPS);
        if (service == null) return;

        var characteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_DPS);
        if (characteristic == null) return;

        Log.d("MyTag", Arrays.toString(buffer));

        characteristic.setValue(buffer);
        gatt.writeCharacteristic(characteristic);
    }

    private String key(int c1, int c2) {
        return c1 + "-" + c2;
    }
}
