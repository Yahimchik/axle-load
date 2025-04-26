package com.mehatronics.axle_load.ble.processor;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIFTY_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SECOND_COMMAND;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public class GattWriteProcessor {

    private final byte[] buffer = new byte[68];

    public void setCommand(int c1, int c2) {

        buffer[0] = (byte) c1;
        buffer[1] = (byte) c2;
        buffer[2] = 0;
        buffer[3] = 0;

        if (c1 == FIFTY_COMMAND) {
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                Log.d("MyTag", Objects.requireNonNull(e.getMessage()));
            }
            String strCodeDUTLoad = "40f97651c07e2bc8b190d3f3ce73a526" + "1a7268fd8bf3d8abcf2599aef364c1a3";
            messageDigest.reset();
            messageDigest.update(strCodeDUTLoad.getBytes(StandardCharsets.UTF_8));
            final byte[] resultByte3 = messageDigest.digest();
            String CodeDUTLoad1 = String.format("%032x", new BigInteger(1, resultByte3)); // SHA-256 (64) MD5 (32)

            if (c2 == FIRST_COMMAND) {
                for (int i = 0; i < 32; i += 2) {
                    buffer[(4 + (i / 2))] = (byte) Integer.parseInt(CodeDUTLoad1.substring(i, (i + 2)), 16);
                }
            }

            if (c2 == SECOND_COMMAND) {
                CodeDUTLoad1 = "3f118b1cd5e6dbd68abeea5713fc61b7";
                for (int i = 0; i < 32; i += 2) {
                    buffer[(4 + (i / 2))] = (byte) Integer.parseInt(CodeDUTLoad1.substring(i, (i + 2)), 16);
                }
            }
//            Log.d("MyTag", Arrays.toString(buffer));
        }
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void clearBuffer() {
        Arrays.fill(buffer, (byte) 0);
    }

    public void write(BluetoothGatt gatt) {
        var service = gatt.getService(USER_SERVICE_DPS);
        if (service == null) return;

        var characteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_DPS);
        if (characteristic == null) return;

        characteristic.setValue(buffer);

        try {
            gatt.writeCharacteristic(characteristic);
        } catch (SecurityException e) {
            Log.d("MyTag", "Security exception: " + e.getMessage());
        }
    }
}
