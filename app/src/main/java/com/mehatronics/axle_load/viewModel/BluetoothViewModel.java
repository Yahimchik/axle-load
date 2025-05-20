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
    private List<CalibrationTable> originalPoints;


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

    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        originalPoints = deviceDetails.getTable();
        aupdateVirtualPoint(deviceDetails);
    }

    public void deletePoint(int position) {
        if (originalPoints != null && position >= 0 && position < originalPoints.size()) {
            originalPoints.remove(position);
            updateTable(new ArrayList<>(originalPoints));
        }
    }

    public void addPoint(CalibrationTable newPoint) {
        if (originalPoints == null) return;
        originalPoints.add(originalPoints.size() - 1, newPoint);
        updateTable(new ArrayList<>(originalPoints));
    }

    private void aupdateVirtualPoint(DeviceDetails deviceDetails) {
        List<CalibrationTable> displayed = new ArrayList<>(originalPoints);
        try {
            if (!displayed.isEmpty()) {
                var virtual = new CalibrationTable(
                        (int) (parsePressure(deviceDetails.getPressure()) * 10),
                        0,
                        true
                );
                displayed.add(displayed.size() - 1, virtual);
            }
            calibrationTableLiveData.setValue(displayed);
        } catch (NumberFormatException e) {
            Log.w("MyTag", "Invalid pressure format: " + deviceDetails.getPressure());
        }
    }

    private void updateTable(List<CalibrationTable> table) {
        calibrationTableLiveData.setValue(new ArrayList<>(table));
    }

    private float parsePressure(String pressure) {
        if (pressure == null || pressure.equalsIgnoreCase("0")) {
            return 0f;
        }
        return Float.parseFloat(pressure);
    }
}

