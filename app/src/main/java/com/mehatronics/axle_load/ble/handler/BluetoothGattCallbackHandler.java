package com.mehatronics.axle_load.ble.handler;

import static com.mehatronics.axle_load.constants.RegexPattern.UUID_PATTERN;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BluetoothGattCallbackHandler extends BluetoothGattCallback {
    private final MutableLiveData<DeviceDetails> deviceDetailsLiveData = new MutableLiveData<>();
    private final List<BluetoothGattCharacteristic> characteristicsQueue = new ArrayList<>();
    private final ConnectionStateListener connectionStateListener;
    private final List<byte[]> values = new ArrayList<>();
    private boolean isActivityStarted = false;
    private boolean isConnected = false;

    public BluetoothGattCallbackHandler(ConnectionStateListener listener) {
        this.connectionStateListener = listener;
    }

    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return deviceDetailsLiveData;
    }

    public void clearDetails() {
        deviceDetailsLiveData.setValue(null);
    }

    public void resetState() {
        isActivityStarted = false;
        isConnected = false;
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
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d("MyTag", "Services discovered:");
            characteristicsQueue.clear();
            for (BluetoothGattService service : gatt.getServices()) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                        characteristicsQueue.add(characteristic);
                    }
                }
            }
            readNextCharacteristic(gatt);
            startReadingPeriodically(gatt);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            byte[] data = characteristic.getValue();
            String UUID = characteristic.getUuid().toString();
            if (data != null && (UUID.matches(UUID_PATTERN) || UUID.equals("83940e89-e38d-4093-ba21-ce6aed75ff1c"))) {
                values.add(data);
                Log.d("MyTag", "Read characteristic: " + Arrays.toString(data));
            }
        }

        readNextCharacteristic(gatt);

        if (isActivityStarted) {
            return;
        }

        if (isConnected && values.size() >= 9) {
            isActivityStarted = true;
            DeviceDetails deviceDetails = createDeviceDetailsObject();
            deviceDetailsLiveData.postValue(deviceDetails);
            Log.d("MyTag", " " + deviceDetails);
            values.clear();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void readNextCharacteristic(BluetoothGatt gatt) {
        if (!characteristicsQueue.isEmpty()) {
            try {
                gatt.readCharacteristic(characteristicsQueue.remove(0));
            } catch (SecurityException e) {
                //
            }
        }
    }

    private DeviceDetails createDeviceDetailsObject() {
        if (values.size() < 8) {
            Log.e("MyTag", "Insufficient data to create DeviceDetails");
            return null;
        }

        Log.d("MyTag", "!!!");
        return new DeviceDetails.Builder()
                .setDeviceName(new String(values.get(0)))
                .setDateManufacturer(extractData(values.get(1)))
                .setManufacturer(new String(values.get(2)))
                .setModelType(new String(values.get(3)))
                .setSerialNumber(new String(values.get(4)))
                .setFirmwareVersion(new String(values.get(5)))
                .setHardWareVersion(new String(values.get(6)))
                .setBatteryLevel(extractData(values.get(7)))
                .setWeight(extractDetails(values.get(values.size() - 1), "weight"))
                .setPressure(extractDetails(values.get(values.size() - 1), "pressure"))
                .build();
    }

    private void writeToCharacteristic(BluetoothGatt gatt) {
        BluetoothGattService service = gatt.getService(UUID.fromString("58c2f7bf-fef8-4b04-8850-a820113120ad"));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("027dd8e6-3310-49dd-a767-444de694117b"));
        if (characteristic != null) {
            byte[] value = new byte[68];
            value[0] = 0x07;
            value[1] = 0x02;
            characteristic.setValue(value);
            try {
                gatt.writeCharacteristic(characteristic);
            } catch (SecurityException e) {
                //
            }
            Log.d("MyTag", "Write to characteristic: " + Arrays.toString(value));
        }
    }

    private BluetoothGattCharacteristic getCharacteristic(BluetoothGatt gatt, String uuid) {
        for (BluetoothGattService service : gatt.getServices()) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic.getUuid().toString().equals(uuid)) {
                    return characteristic;
                }
            }
        }
        return null;
    }

    public void startReadingPeriodically(BluetoothGatt gatt) {
        new Thread(() -> {
            while (isConnected()) {
                try {
                    writeToCharacteristic(gatt);
                    Thread.sleep(2000);
                    BluetoothGattService service = gatt.getService(UUID.fromString("58c2f7bf-fef8-4b04-8850-a820113120ad"));
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("83940e89-e38d-4093-ba21-ce6aed75ff1c"));
                    if (characteristic != null) {
                        try {
                            gatt.readCharacteristic(characteristic);
                            Log.d("MyTag1", "---" + Arrays.toString(characteristic.getValue()));
                        } catch (SecurityException e) {
                            Log.e("MyTag", "SecurityException while reading characteristic: " + e.getMessage());
                        }
                        Log.d("MyTag", "Reading characteristic: " + characteristic.getUuid());
                    }
                } catch (InterruptedException e) {
                    Log.e("MyTag", "Thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }
}