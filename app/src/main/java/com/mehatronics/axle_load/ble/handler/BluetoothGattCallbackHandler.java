package com.mehatronics.axle_load.ble.handler;

import static com.mehatronics.axle_load.constants.UuidConstants.READ_CHARACTERISTIC;
import static com.mehatronics.axle_load.constants.UuidConstants.SERVICE;
import static com.mehatronics.axle_load.constants.UuidConstants.WRITE_CHARACTERISTIC;
import static com.mehatronics.axle_load.utils.DataUtils.extractData;
import static com.mehatronics.axle_load.utils.DataUtils.extractDetails;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.entities.DeviceDetails;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BluetoothGattCallbackHandler extends BluetoothGattCallback {
    private final MutableLiveData<DeviceDetails> deviceDetailsLiveData = new MutableLiveData<>();
    private final Queue<BluetoothGattCharacteristic> characteristicsQueue = new LinkedList<>();
    private final ConnectionStateListener connectionStateListener;
    private final List<byte[]> values = new LinkedList<>();
    private boolean isReadingAllCharacteristics = false;
    private boolean isConnected = false;

    public BluetoothGattCallbackHandler(ConnectionStateListener listener) {
        this.connectionStateListener = listener;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            Log.d("MyTag", "Connected to device");
            values.clear();
            try {
                gatt.discoverServices();
            } catch (SecurityException e) {
                Log.e("MyTag", "SecurityException: " + e.getMessage());
            }
            connectionStateListener.onConnected();
            isConnected = true;
        } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            Log.d("MyTag", "Disconnected from device");
            connectionStateListener.onDisconnected();
            isConnected = false;
            values.clear();
            deviceDetailsLiveData.setValue(null);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d("MyTag", "Services discovered, starting to read all characteristics...");
            readAllCharacteristics(gatt);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            byte[] data = characteristic.getValue();
            values.add(data);
            Log.d("MyTag", "Read characteristic " + characteristic.getUuid() + ": " + Arrays.toString(data));

            if (isReadingAllCharacteristics) {
                readNextCharacteristic(gatt);
            } else {
                if (isConnected && values.size() >= 9) {
                    DeviceDetails deviceDetails = createDeviceDetailsObject();
                    deviceDetailsLiveData.postValue(deviceDetails);
                }
                writeAndRead(gatt);
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d("MyTag", "Write successful: " + characteristic.getUuid());
            if (characteristic.getUuid().equals(WRITE_CHARACTERISTIC)) {
                BluetoothGattService service = gatt.getService(SERVICE);

                var readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC);
                if (readCharacteristic != null) {
                    try {
                        gatt.readCharacteristic(readCharacteristic);
                    } catch (SecurityException e) {
                        //
                    }
                }
            }
        }
    }

    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return deviceDetailsLiveData;
    }

    public void clearDetails() {
        deviceDetailsLiveData.setValue(null);
    }

    public void resetState() {
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void readAllCharacteristics(BluetoothGatt gatt) {
        characteristicsQueue.clear();
        for (BluetoothGattService service : gatt.getServices()) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                    characteristicsQueue.add(characteristic);
                }
            }
        }
        isReadingAllCharacteristics = true;
        readNextCharacteristic(gatt);
    }

    private void readNextCharacteristic(BluetoothGatt gatt) {
        if (!characteristicsQueue.isEmpty()) {
            BluetoothGattCharacteristic characteristic = characteristicsQueue.poll();
            if (characteristic != null) {
                try {
                    gatt.readCharacteristic(characteristic);
                } catch (SecurityException e) {
                    //
                }
            }
        } else {
            isReadingAllCharacteristics = false;
            Log.d("MyTag", "Finished reading all characteristics, starting periodic write-read...");
            writeAndRead(gatt);
        }
    }

    private void writeAndRead(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(SERVICE);
        if (service == null) return;

        var writeCharacteristic = service.getCharacteristic(WRITE_CHARACTERISTIC);
        var readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC);

        if (writeCharacteristic == null || readCharacteristic == null) return;

        byte[] value = new byte[68];
        value[0] = 0x07;
        value[1] = 0x02;
        writeCharacteristic.setValue(value);

        try {
            if (gatt.writeCharacteristic(writeCharacteristic)) {
                Log.d("MyTag", "Write successful, will read after...");
            } else {
                Log.e("MyTag", "Write failed");
            }
        } catch (SecurityException e) {
            //
        }
    }

    private DeviceDetails createDeviceDetailsObject() {
        if (values.size() < 8) {
            Log.e("MyTag", "Insufficient data to create DeviceDetails");
            return null;
        }

        return new DeviceDetails.Builder()
                .setDeviceName(new String(values.get(2)))
                .setDateManufacturer(extractData(values.get(3)))
                .setManufacturer(new String(values.get(4)))
                .setModelType(new String(values.get(5)))
                .setSerialNumber(new String(values.get(6)))
                .setFirmwareVersion(new String(values.get(7)))
                .setHardWareVersion(new String(values.get(8)))
                .setBatteryLevel(extractData(values.get(9)))
                .setWeight(extractDetails(values.get(values.size() - 1), "weight"))
                .setPressure(extractDetails(values.get(values.size() - 1), "pressure"))
                .build();
    }
}
