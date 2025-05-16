package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.utils.format.SensorConfigFormat;

public class SensorConfigureViewModel extends ViewModel {

    private final MutableLiveData<String> messageDeliveryPeriod = new MutableLiveData<>();
    private final MutableLiveData<String> measurementPeriod = new MutableLiveData<>();
    private final MutableLiveData<String> stateNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> installationPoint = new MutableLiveData<>();

    public void bind(SensorConfig config) {
        messageDeliveryPeriod.setValue(SensorConfigFormat.setMessageDeliveryPeriod(config));
        measurementPeriod.setValue(SensorConfigFormat.setMeasurementPeriod(config));
        stateNumber.setValue(SensorConfigFormat.setStateNumber(config));
        installationPoint.setValue(config.getInstallationPoint());
    }

    public void updateMessageDeliveryPeriod(String value) {
        messageDeliveryPeriod.setValue(value);
    }

    public void updateMeasurementPeriod(String value) {
        measurementPeriod.setValue(value);
    }

    public void updateStateNumber(String value) {
        stateNumber.setValue(value);
    }

    public void updateInstallationPoint(int point) {
        installationPoint.setValue(point);
    }

    public LiveData<String> getMessageDeliveryPeriod() {
        return messageDeliveryPeriod;
    }

    public LiveData<String> getMeasurementPeriod() {
        return measurementPeriod;
    }

    public LiveData<String> getStateNumber() {
        return stateNumber;
    }

    public LiveData<Integer> getInstallationPoint() {
        return installationPoint;
    }

    public SensorConfig toSensorConfig(SensorConfig sensorConfig) {
        sensorConfig.setMessageDeliveryPeriod(Integer.parseInt(messageDeliveryPeriod.getValue()));
        sensorConfig.setMeasurementPeriod(Integer.parseInt(measurementPeriod.getValue()));
        sensorConfig.setStateNumber(stateNumber.getValue());
        sensorConfig.setInstallationPoint(installationPoint.getValue() != null ? installationPoint.getValue() : 1);
        return sensorConfig;
    }
}
