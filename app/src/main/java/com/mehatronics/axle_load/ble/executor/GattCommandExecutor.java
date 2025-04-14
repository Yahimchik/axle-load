package com.mehatronics.axle_load.ble.executor;

import static com.mehatronics.axle_load.utils.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.command.CommandStateHandler;

import java.util.Arrays;

public class GattCommandExecutor {

    private final byte[] writeBuffer = new byte[68];
    private CommandStateHandler commandStateHandler;
    private BluetoothGattCallbackHandler handler;

    public GattCommandExecutor(CommandStateHandler initialStateHandler, BluetoothGattCallbackHandler handler) {
        this.commandStateHandler = initialStateHandler;
        this.handler = handler;
    }

    public void setCommandState(CommandStateHandler newState) {
        this.commandStateHandler = newState;
    }

    public void execute(BluetoothGatt gatt) {
        Arrays.fill(writeBuffer, (byte) 0);
        commandStateHandler.handle(gatt, handler);
        write(gatt);
    }

    public void setCommand(int c1, int c2) {
        writeBuffer[0] = (byte) c1;
        writeBuffer[1] = (byte) c2;
        writeBuffer[2] = 0;
        writeBuffer[3] = 0;
    }

    public byte[] getBuffer() {
        return writeBuffer;
    }

    private void write(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(USER_SERVICE_DPS);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_DPS);
        characteristic.setValue(writeBuffer);
        try {
            gatt.writeCharacteristic(characteristic);
        } catch (SecurityException e) {
            Log.d("MyTag", "Security exception: " + e.getMessage());
        }
    }
}
