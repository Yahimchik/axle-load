package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

public class DeviceDetailsViewModel extends ViewModel {

    private final MutableLiveData<DeviceDetails> deviceDetails = new MutableLiveData<>();
    private final MutableLiveData<SensorConfig> sensorConfig = new MutableLiveData<>();

    public LiveData<DeviceDetails> getDeviceDetails() {
        return deviceDetails;
    }

    public LiveData<SensorConfig> getSensorConfig() {
        return sensorConfig;
    }

    public void updateDeviceDetails(DeviceDetails details) {
        deviceDetails.setValue(details);
    }

    public void updateSensorConfig(SensorConfig config) {
        sensorConfig.setValue(config);
    }
}
