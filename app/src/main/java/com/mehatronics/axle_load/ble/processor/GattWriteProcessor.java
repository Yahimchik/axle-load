package com.mehatronics.axle_load.ble.processor;

import static com.mehatronics.axle_load.utils.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.Arrays;

public class GattWriteProcessor {

    private final byte[] buffer = new byte[68];

    public void setCommand(int c1, int c2) {
        buffer[0] = (byte) c1;
        buffer[1] = (byte) c2;
        buffer[2] = 0;
        buffer[3] = 0;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void clearBuffer() {
        Arrays.fill(buffer, (byte) 0);
    }

    public void write(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(USER_SERVICE_DPS);
        if (service == null) return;

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_DPS);
        if (characteristic == null) return;

        characteristic.setValue(buffer);

        try {
            gatt.writeCharacteristic(characteristic);
        } catch (SecurityException e) {
            Log.d("MyTag", "Security exception: " + e.getMessage());
        }
    }
}
