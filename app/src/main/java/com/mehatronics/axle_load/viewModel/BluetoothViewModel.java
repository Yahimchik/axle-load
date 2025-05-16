package com.mehatronics.axle_load.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.ble.repository.BluetoothRepository;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.entities.enums.DeviceType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BluetoothViewModel extends ViewModel {
    private final MutableLiveData<List<CalibrationTable>> calibrationTableLiveData = new MutableLiveData<>();
    private final BluetoothRepository bluetoothRepository;

    @Inject
    public BluetoothViewModel(BluetoothRepository bluetoothRepository) {
        this.bluetoothRepository = bluetoothRepository;
    }

    public LiveData<Boolean> isConnectedLiveData() {
        return bluetoothRepository.isConnectedLiveData();
    }

    public LiveData<List<Device>> getScannedDevices() {
        return bluetoothRepository.getScannedDevices();
    }

    public void clearScannedDevices() {
        bluetoothRepository.clearScannedDevices();
    }

    public void startScan(DeviceType deviceType) {
        bluetoothRepository.startScan(deviceType);
    }

    public void stopScan() {
        bluetoothRepository.stopScan();
    }

    public LiveData<DeviceDetails> getDeviceDetails() {
        return bluetoothRepository.getDeviceDetailsLiveData();
    }

    public LiveData<SensorConfig> getSensorConfigure() {
        return bluetoothRepository.getSensorConfigureLiveData();
    }

    public void connectToDevice(Device device) {
        bluetoothRepository.connectToDevice(device);
    }

    public void disconnect() {
        bluetoothRepository.disconnect();
    }

    public void clearDetails() {
        bluetoothRepository.clearDetails();
    }

    public void saveSensorConfiguration() {
        SensorConfig config = getSensorConfigure().getValue();
        if (config != null) {
            bluetoothRepository.saveConfiguration();
        } else {
            Log.w("MyTag", "Sensor config is null");
        }
    }

    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return calibrationTableLiveData;
    }

    public void updateCalibrationTable(List<CalibrationTable> table) {
        calibrationTableLiveData.setValue(table);
    }

    public List<CalibrationTable> getExtendedCalibrationTableWithVirtualPoint(DeviceDetails deviceDetails) {
        List<CalibrationTable> originalTable = deviceDetails.getTable();
        if (originalTable == null || originalTable.size() < 2) {
            return originalTable;
        }

        List<CalibrationTable> extendedTable = new ArrayList<>(originalTable);

        try {
            float currentPressure = parsePressure(deviceDetails.getPressure());

            CalibrationTable first = originalTable.get(0);
            CalibrationTable last = originalTable.get(originalTable.size() - 1);

            int detectorValue = (int) (currentPressure * 10);
            float multiplier = 10f / (last.getDetector() - first.getDetector());

            CalibrationTable virtualPoint = new CalibrationTable(detectorValue, multiplier);

            extendedTable.add(extendedTable.size() - 1, virtualPoint);

        } catch (NumberFormatException e) {
            Log.w("MyTag", "Invalid pressure format: " + deviceDetails.getPressure());
        }

        return extendedTable;
    }

    public void deleteCalibrationPoint(int position) {
        List<CalibrationTable> currentTable = calibrationTableLiveData.getValue();
        if (currentTable != null && position >= 0 && position < currentTable.size()) {
            currentTable.remove(position);
            calibrationTableLiveData.setValue(new ArrayList<>(currentTable));
        }
    }

    public void addCalibrationPoint(int position, CalibrationTable newPoint) {
        List<CalibrationTable> currentTable = calibrationTableLiveData.getValue();
        if (currentTable != null && position >= 0 && position <= currentTable.size()) {
            currentTable.add(position, newPoint);
            calibrationTableLiveData.setValue(new ArrayList<>(currentTable));
        }
    }

    private float parsePressure(String pressure) {
        if (pressure == null || pressure.equalsIgnoreCase("0")) {
            return 0f; // дефолтное давление
        }
        return Float.parseFloat(pressure);
    }

}

