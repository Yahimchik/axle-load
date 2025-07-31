package com.mehatronics.axle_load.data.repository.impl;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.data.repository.DeviceRepository;
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
public class DeviceRepositoryImpl implements DeviceRepository {
    private final AxisService axisService;
    private final SensorService sensorService;

    @Inject
    public DeviceRepositoryImpl(AxisService axisService, SensorService sensorService) {
        this.axisService = axisService;
        this.sensorService = sensorService;
    }

    @Override
    public LiveData<List<AxisModel>> getAxisList() {
        return axisService.getAxisList();
    }

    @Override
    public int getAxisCount() {
        return axisService.getAxisCount();
    }

    @Override
    public LiveData<String> getMessage() {
        return axisService.getMessage();
    }

    @Override
    public void setDeviceToAxis(int axisNumber, AxisSide side, String mac) {
        axisService.setDeviceToAxis(axisNumber, side, mac);
    }

    @Override
    public void resetDevicesForAxis(int axisNumber) {
        axisService.resetDevicesForAxis(axisNumber);
    }

    @Override
    public String getMacForAxisSide(int axisNumber, AxisSide side) {
        return axisService.getMacForAxisSide(axisNumber, side);
    }

    @Override
    public void onConfigureClicked(String input) {
        axisService.onConfigureClicked(input);
    }

    @Override
    public LiveData<Event<InstalationPoint>> getAxisClick() {
        return axisService.getAxisClick();
    }

    @Override
    public void onWheelClicked(int axisNumber, AxisSide side) {
        axisService.onWheelClicked(axisNumber, side);
    }

    @Override
    public Set<String> getMacsForAxis(int axisNumber) {
        return axisService.getMacsForAxis(axisNumber);
    }

    @Override
    public void setSnackBarCallback(MessageCallback messageCallback) {
        sensorService.setSnackBarCallback(messageCallback);
    }

    @Override
    public LiveData<List<Device>> getScannedDevicesLiveData() {
        return sensorService.getScannedDevicesLiveData();
    }

    @Override
    public void updateScannedDevices(List<Device> newDevices) {
        sensorService.updateScannedDevices(newDevices);
    }

    @Override
    public void markMacAsSelected(Device device) {
        sensorService.markMacAsSelected(device);
    }

    @Override
    public void resetSelectedDevices() {
        sensorService.resetSelectedDevices();
    }

    @Override
    public void resetSelectedDevicesByMacs(Set<String> macs) {
        sensorService.resetSelectedDevicesByMacs(macs);
    }

    @Override
    public LiveData<Boolean> getSavedStateLiveData() {
        return sensorService.getSavedStateLiveData();
    }

    @Override
    public void markAsSaved() {
        sensorService.markAsSaved();
    }

    @Override
    public void markAsUnsaved() {
        sensorService.markAsUnsaved();
    }

    @Override
    public void clearMacs() {
        sensorService.clearMacs();
    }

    @Override
    public LiveData<Set<String>> getConfiguredMacs() {
        return sensorService.getConfiguredMacs();
    }

    @Override
    public void addConfiguredMac(String mac) {
        sensorService.addConfiguredMac(mac);
    }

    @Override
    public void setLastConfiguredMac(String mac) {
        sensorService.setLastConfiguredMac(mac);
    }

    @Override
    public LiveData<String> getLastConfiguredMac() {
        return sensorService.getLastConfiguredMac();
    }

    @Override
    public LiveData<Boolean> getSelectionModeLiveData() {
        return sensorService.getSelectionModeLiveData();
    }

    @Override
    public void setSelectionMode(boolean isSelection) {
        sensorService.setSelectionMode(isSelection);
    }
}