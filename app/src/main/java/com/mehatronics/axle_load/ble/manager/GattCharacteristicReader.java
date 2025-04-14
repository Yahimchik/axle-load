//package com.mehatronics.axle_load.ble.manager;
//
//import static com.mehatronics.axle_load.utils.constants.UuidConstants.USER_SERVICE_DPS;
//import static com.mehatronics.axle_load.utils.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;
//
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattService;
//import android.util.Log;
//
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.Queue;
//
//public class GattCharacteristicReader {
//
//    private final Queue<BluetoothGattCharacteristic> queue = new LinkedList<>();
//    private boolean isReading = false;
//    private byte[] value;
//
//    public GattCharacteristicReader(byte[] value) {
//        this.value = value;
//    }
//
//    public void readAll(BluetoothGatt gatt) {
//        queue.clear();
//        for (BluetoothGattService service : gatt.getServices()) {
//            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
//                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
//                    queue.add(characteristic);
//                }
//            }
//        }
//        isReading = true;
//        readNext(gatt);
//    }
//
//    public void readNext(BluetoothGatt gatt) {
//        if (!queue.isEmpty()) {
//            BluetoothGattCharacteristic characteristic = queue.poll();
//            try {
//                gatt.readCharacteristic(characteristic);
//            } catch (SecurityException e) {
//                Log.e("MyTag", "SecurityException: " + e.getMessage());
//            }
//        } else {
//            isReading = false;
//            writeToCharacteristic(gatt);
//        }
//    }
//
//    public void writeToCharacteristic(BluetoothGatt gatt) {
//        Arrays.fill(value, (byte) 0);
//
//        var service = gatt.getService(USER_SERVICE_DPS);
//        var writeCharacteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_DPS);
//
//        determineAndSetCommand(gatt);
//        writeCharacteristic.setValue(value);
//
//        try {
//            gatt.writeCharacteristic(writeCharacteristic);
//        } catch (SecurityException e) {
//            Log.d("MyTag", "Security exception: " + e.getMessage());
//        }
//    }
//
//    public boolean isReading() {
//        return isReading;
//    }
//}
