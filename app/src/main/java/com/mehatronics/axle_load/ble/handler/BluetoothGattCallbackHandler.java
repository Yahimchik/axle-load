package com.mehatronics.axle_load.ble.handler;

import static com.mehatronics.axle_load.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToBattery;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToDate;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToString;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToValue;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SECOND_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.READ_CHARACTERISTIC_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.entities.DeviceDetails;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BluetoothGattCallbackHandler extends BluetoothGattCallback {
    private final MutableLiveData<DeviceDetails> deviceDetailsLiveData = new MutableLiveData<>();
    private final Queue<BluetoothGattCharacteristic> characteristicsQueue = new LinkedList<>();
    private final List<byte[]> values = new LinkedList<>();
    private boolean isReadingAllCharacteristics = false;
    private boolean isConnected = false;
    private int size;

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (isStatusOk(newState, BluetoothGatt.STATE_CONNECTED)) {
            Log.d("MyTag", "Connected to device");
            values.clear();
            isConnected = true;
            try {
                gatt.discoverServices();
            } catch (SecurityException e) {
                Log.e("MyTag", "SecurityException: " + e.getMessage());
            }
        } else if (isStatusOk(newState, BluetoothGatt.STATE_DISCONNECTED)) {
            Log.d("MyTag", "Disconnected from device");
            isConnected = false;
            values.clear();
            deviceDetailsLiveData.setValue(null);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            Log.d("MyTag", "Services discovered, starting to read all characteristics...");
            readAllCharacteristics(gatt);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            values.add(characteristic.getValue());
            if (isReadingAllCharacteristics) {
                readNextCharacteristic(gatt);
            } else {
                if (isConnected && values.size() >= 9) {
                    size = values.size() - 1;
                    deviceDetailsLiveData.postValue(createDeviceDetailsObject());
                }
                writeAndRead(gatt);
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (isStatusOk(status, BluetoothGatt.GATT_SUCCESS)) {
            if (characteristic.getUuid().equals(WRITE_CHARACTERISTIC_DPS)) {
                BluetoothGattService service = gatt.getService(USER_SERVICE_DPS);

                var readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_DPS);
                if (readCharacteristic != null) {
                    try {
                        gatt.readCharacteristic(readCharacteristic);
                    } catch (SecurityException e) {
                        Log.d("MyTag", "Security exception: " + e.getMessage());
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

    private boolean isStatusOk(int actual, int expected) {
        return actual == expected;
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
            var characteristic = characteristicsQueue.poll();
            if (characteristic != null) {
                try {
                    gatt.readCharacteristic(characteristic);
                } catch (SecurityException e) {
                    Log.d("MyTag", "Security exception: " + e.getMessage());
                }
            }
        } else {
            isReadingAllCharacteristics = false;
            Log.d("MyTag", "Finished reading all characteristics, starting periodic write-read...");
            writeAndRead(gatt);
        }
    }

    private void writeAndRead(BluetoothGatt gatt) {
        var service = gatt.getService(USER_SERVICE_DPS);

        var writeCharacteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_DPS);

        byte[] value = new byte[68];

        value[0] = SEVEN_COMMAND;
        value[1] = SECOND_COMMAND;

        writeCharacteristic.setValue(value);

        try {
            gatt.writeCharacteristic(writeCharacteristic);
        } catch (SecurityException e) {
            Log.d("MyTag", "Security exception: " + e.getMessage());
        }
    }

    private DeviceDetails createDeviceDetailsObject() {
        if (values.size() < 8) {
            return null;
        }

        String deviceName = convertBytesToString(values.get(2));
        String dateManufacture = convertBytesToDate(values.get(3));
        String manufacturer = convertBytesToString(values.get(4));
        String modelType = convertBytesToString(values.get(5));
        String serialNumber = convertBytesToString(values.get(6));
        String firmwareVersion = convertBytesToString(values.get(7));
        String hardwareVersion = convertBytesToString(values.get(8));
        String batteryLevel = convertBytesToBattery(values.get(9));
        String weight = convertBytesToValue(values.get(size), WEIGHT);
        String pressure = convertBytesToValue(values.get(size), PRESSURE);

        return new DeviceDetails.Builder()
                .setDeviceName(deviceName)
                .setDateManufacturer(dateManufacture)
                .setManufacturer(manufacturer)
                .setModelType(modelType)
                .setSerialNumber(serialNumber)
                .setFirmwareVersion(firmwareVersion)
                .setHardWareVersion(hardwareVersion)
                .setBatteryLevel(batteryLevel)
                .setWeight(weight)
                .setPressure(pressure)
                .build();
    }
}
