package com.mehatronics.axle_load.ble.processor;

import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToCalibrationTable;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToConfiguration;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.READ_CHARACTERISTIC_DPS;
import static com.mehatronics.axle_load.utils.constants.UuidConstants.USER_SERVICE_DPS;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.ble.parser.GattDataParser;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.utils.CalibrationParseResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.inject.Inject;

public class GattReadProcessor {
    private final Queue<BluetoothGattCharacteristic> characteristicsQueue = new LinkedList<>();
    private final MutableLiveData<DeviceDetails> deviceDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<SensorConfig> sensorConfigLiveData = new MutableLiveData<>();
    private final GattDataParser gattDataParser = new GattDataParser();
    private final List<CalibrationTable> table = new ArrayList<>();
    private final List<byte[]> values = new ArrayList<>();
    private boolean isRieadingConfigComplete = false;
    private boolean isReadingTableComplete = false;
    private boolean isConfigurationSaved = false;
    private boolean isReadingAll = false;
    private boolean isTableSaved = false;
    private boolean isConnected = false;
    private int tablePage = 0;

    @Inject
    public GattReadProcessor() {

    }

    public int getTablePage() {
        return tablePage;
    }

    public void readAllCharacteristics(BluetoothGatt gatt) {
        characteristicsQueue.clear();
        for (var service : gatt.getServices()) {
            for (var characteristic : service.getCharacteristics()) {
                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                    characteristicsQueue.add(characteristic);
                }
            }
        }
        isReadingAll = true;
        isRieadingConfigComplete = true;
        isReadingTableComplete = true;
        readNext(gatt);
    }

    public void handleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();

        if (values.size() < 11) {
            values.add(bytes);
        } else if (!Arrays.equals(values.get(values.size() - 1), bytes)) {
            values.set(values.size() - 1, bytes);
        }

        if (isReadingAll) {
            readNext(gatt);
            return;
        }

        if (isRieadingConfigComplete && isMatchingCommand(bytes, 0, SEVEN_COMMAND)
                && isMatchingCommand(bytes, 1, FIRST_COMMAND)) {
            isRieadingConfigComplete = false;
            sensorConfigLiveData.postValue(convertBytesToConfiguration(bytes));
        }

        if (isReadingTableComplete && isMatchingCommand(bytes, 0, FIRST_COMMAND)) {
            var result = convertBytesToCalibrationTable(bytes, table, tablePage);
            tablePage = result.nextPage;
            if (result.tableCompleted) {
                isReadingTableComplete = false;
            }
        }

        if (isConnected && values.size() > 8) {
            deviceDetailsLiveData.postValue(gattDataParser.parseDeviceDetails(values, table));
        }
    }

    public void rereadCalibrationTable() {
        isReadingTableComplete = true;
        tablePage = 0;
        table.clear();
    }

    public void readNextAfterWrite(BluetoothGatt gatt) {
        var service = gatt.getService(USER_SERVICE_DPS);
        var readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_DPS);
        if (readCharacteristic != null) {
            try {
                gatt.readCharacteristic(readCharacteristic);
            } catch (SecurityException e) {
                Log.d("MyTag", "Security exception: " + e.getMessage());
            }
        }
    }

    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return deviceDetailsLiveData;
    }

    public void setDeviceDetailsLiveData(DeviceDetails details) {
        deviceDetailsLiveData.setValue(details);
    }

    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return sensorConfigLiveData;
    }

    public void clearDetails() {
        deviceDetailsLiveData.setValue(null);
    }

    public void setConfigurationSaved(boolean value) {
        isConfigurationSaved = value;
    }

    public void setTableSaved(boolean value){
        isTableSaved = value;
    }

    public boolean isTableSaved() {
        return isTableSaved;
    }

    public boolean isConfigurationSaved() {
        return isConfigurationSaved;
    }

    public void updateState(boolean isConnected) {
        this.isConnected = isConnected;
        values.clear();
        table.clear();
    }

    public boolean isReadingAll() {
        return isReadingAll;
    }

    private void readNext(BluetoothGatt gatt) {
        if (!characteristicsQueue.isEmpty()) {
            BluetoothGattCharacteristic next = characteristicsQueue.poll();
            if (next != null) {
                try {
                    gatt.readCharacteristic(next);
                } catch (SecurityException e) {
                    Log.d("MyTag", "Security exception: " + e.getMessage());
                }
            }
        } else {
            isReadingAll = false;
            Log.d("MyTag", "Finished reading all characteristics.");
        }
    }

    private boolean isMatchingCommand(byte[] bytes, int index, int command) {
        return (bytes[index] & ZERO_COMMAND_BINARY) == command;
    }
}