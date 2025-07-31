package com.mehatronics.axle_load.data.repository;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.data.service.AxisService;
import com.mehatronics.axle_load.data.service.SensorService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceRepository {
    private final AxisService axisService;
    private final SensorService sensorService;

    @Inject
    public DeviceRepository(AxisService axisService, SensorService sensorService) {
        this.axisService = axisService;
        this.sensorService = sensorService;
    }

    public LiveData<List<AxisModel>> getAxisList() {
        return axisService.getAxisList();
    }

    public int getAxisCount(){
        return axisService.getAxisCount();
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

    public LiveData<Boolean> getSavedStateLiveData() {
        return sensorService.getSavedStateLiveData();
    }

    public void markAsSaved() {
        sensorService.markAsSaved();
    }

    public void markAsUnsaved() {
        sensorService.markAsUnsaved();
    }

    public void clearMacs() {
        sensorService.clearMacs();
    }

    public LiveData<Set<String>> getConfiguredMacs() {
        return sensorService.getConfiguredMacs();
    }

    public void addConfiguredMac(String mac) {
        sensorService.addConfiguredMac(mac);
    }

    public void setLastConfiguredMac(String mac) {
        sensorService.setLastConfiguredMac(mac);
    }

    public LiveData<String> getLastConfiguredMac() {
        return sensorService.getLastConfiguredMac();
    }
}