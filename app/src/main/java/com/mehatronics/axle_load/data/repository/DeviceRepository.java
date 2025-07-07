package com.mehatronics.axle_load.data.repository;

import android.Manifest;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.data.service.AxisService;
import com.mehatronics.axle_load.data.service.BleScannerService;
import com.mehatronics.axle_load.data.service.SensorService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.entities.enums.ValidationError;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceRepository {
    private final AxisService axisService;
    private final SensorService sensorService;
    private final BleScannerService bleScannerService;
    private final MutableLiveData<Boolean> savedStateLiveData = new MutableLiveData<>(false);

    @Inject
    public DeviceRepository(AxisService axisService, SensorService sensorService, BleScannerService bleScannerService) {
        this.axisService = axisService;
        this.sensorService = sensorService;
        this.bleScannerService = bleScannerService;
    }

    public LiveData<List<AxisModel>> getAxisList() {
        return axisService.getAxisList();
    }

    public LiveData<String> getMessage() {
        return axisService.getMessage();
    }

    public void setDeviceToAxis(int axisNumber, AxisSide side, String mac) {
        axisService.setDeviceToAxis(axisNumber, side, mac);
    }

    public void resetDevicesForAxis(int axisNumber) {
        axisService.resetDevicesForAxis(axisNumber);
    }

    public String getMacForAxisSide(int axisNumber, AxisSide side) {
        return axisService.getMacForAxisSide(axisNumber, side);
    }

    public void onConfigureClicked(String input) {
        axisService.onConfigureClicked(input);
    }

    public LiveData<Event<InstalationPoint>> getAxisClick() {
        return axisService.getAxisClick();
    }

    public void onWheelClicked(int axisNumber, AxisSide side) {
        axisService.onWheelClicked(axisNumber, side);
    }

    public String getErrorMessage(ValidationError error) {
        return axisService.getErrorMessage(error);
    }

    public AxisModel cloneWithUpdatedDevice(AxisModel model, AxisSide sideToUpdate, String mac) {
        return axisService.cloneWithUpdatedDevice(model, sideToUpdate, mac);
    }

    public Set<String> getMacsForAxis(int axisNumber) {
        return axisService.getMacsForAxis(axisNumber);
    }

    public void setSnackBarCallback(MessageCallback messageCallback) {
        sensorService.setSnackBarCallback(messageCallback);
    }

    public LiveData<List<Device>> getScannedDevicesLiveData() {
        return sensorService.getScannedDevicesLiveData();
    }

    public void updateScannedDevices(List<Device> newDevices) {
        sensorService.updateScannedDevices(newDevices);
    }

    public void markMacAsSelected(Device device) {
        sensorService.markMacAsSelected(device);
    }

    public void resetSelectedDevices() {
        sensorService.resetSelectedDevices();
    }

    public void resetSelectedDevicesByMacs(Set<String> macs) {
        sensorService.resetSelectedDevicesByMacs(macs);
    }

    public boolean isMacSelected(String mac) {
        return sensorService.isMacSelected(mac);
    }

    public LiveData<List<Device>> getScannedDevices() {
        return bleScannerService.getScannedDevices();
    }

    public LiveData<Boolean> getSavedStateLiveData() {
        return savedStateLiveData;
    }

    public void markAsSaved() {
        savedStateLiveData.setValue(true);
    }

    public void markAsUnsaved() {
        savedStateLiveData.setValue(false);
    }
}