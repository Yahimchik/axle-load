package com.mehatronics.axle_load.data.repository.impl;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.data.repository.DeviceRepository;
import com.mehatronics.axle_load.data.service.AxisService;
import com.mehatronics.axle_load.data.service.SensorService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceRepositoryImpl implements DeviceRepository {
    private final AxisService axisService;
    private final SensorService sensorService;
    private String stateNumber;

    @Inject
    public DeviceRepositoryImpl(AxisService axisService, SensorService sensorService) {
        this.axisService = axisService;
        this.sensorService = sensorService;
    }

    @Override
    public LiveData<List<AxisModel>> getAxisList() {
        return axisService.getAxisList();
    }

    public void setLoadedAxisList(List<AxisModel> list) {
        axisService.setLoadedAxisList(list);
        sensorService.resetSelectedDevices();

        List<Device> scannedDevices = sensorService.getScannedDevicesLiveData().getValue();
        if (scannedDevices == null) return;

        Log.d("MyTag", String.valueOf(scannedDevices));

        Set<String> targetMacs = list.stream()
                .flatMap(model -> model.getSideDeviceMap().values().stream())
                .collect(Collectors.toSet());

        for (Device device : scannedDevices) {
            String mac = device.getDevice().getAddress();
            if (targetMacs.contains(mac)) {
                sensorService.markMacAsSelected(device);
            }
        }

        sensorService.refreshScannedDevices();
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
    public void setDeviceName(String name) {
        sensorService.setDeviceName(name);
    }

    @Override
    public String getDeviceName() {
        return sensorService.getDeviceName();
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

    public void refreshScannedDevices() {
        sensorService.refreshScannedDevices();
    }

    @Override
    public void setStateNumber(String stateNumber) {
        this.stateNumber = stateNumber;
    }

    @Override
    public String getStateNumber() {
        return stateNumber == null ? "" : stateNumber;
    }
}