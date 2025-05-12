package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.utils.CalibrationTableManager;

import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

public class DeviceDetailsViewModel extends ViewModel {

    private final MutableLiveData<DeviceDetails> deviceDetails = new MutableLiveData<>();
    private final MutableLiveData<SensorConfig> sensorConfig = new MutableLiveData<>();
    private final MutableLiveData<List<CalibrationTable>> calibrationTable = new MutableLiveData<>();
    private final MutableLiveData<CalibrationTable> virtualPoint = new MutableLiveData<>();

    private final CalibrationTableManager calibrationTableManager;

    @Inject
    public DeviceDetailsViewModel(CalibrationTableManager calibrationTableManager) {
        this.calibrationTableManager = calibrationTableManager;
    }

    // region Getters

    public LiveData<DeviceDetails> getDeviceDetails() {
        return deviceDetails;
    }

    public LiveData<SensorConfig> getSensorConfig() {
        return sensorConfig;
    }

    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return calibrationTable;
    }

    public LiveData<CalibrationTable> getVirtualPoint() {
        return virtualPoint;
    }

    // endregion

    // region Initial Binding

    public void bindDevice(DeviceDetails details) {
        deviceDetails.setValue(details);
        sensorConfig.setValue(details.getSensorConfig());

        List<CalibrationTable> extended = calibrationTableManager.extendCalibrationTable(
                details.getTable(),
                details.getPressure()
        );
        calibrationTable.setValue(extended);

        CalibrationTable virtual = calibrationTableManager.getVirtualPoint(
                details.getTable(),
                details.getPressure()
        );
        virtualPoint.setValue(virtual);
    }

    // endregion

    // region Update methods

    public void updateSensorField(Function<SensorConfig, SensorConfig> updater) {
        SensorConfig current = sensorConfig.getValue();
        if (current != null) {
            SensorConfig updated = updater.apply(current);
            sensorConfig.setValue(updated);
        }
    }

    public void updateVirtualPoint() {
        DeviceDetails details = deviceDetails.getValue();
        if (details != null) {
            CalibrationTable virtual = calibrationTableManager.getVirtualPoint(
                    details.getTable(),
                    details.getPressure()
            );
            virtualPoint.setValue(virtual);
        }
    }
    public void updateSensorConfig(SensorConfig updated) {
        sensorConfig.setValue(updated);
    }
}
