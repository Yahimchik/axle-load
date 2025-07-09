package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.R.string.selected;

import android.Manifest;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.data.service.SensorService;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class SensorServiceImpl implements SensorService {
    private final MutableLiveData<Set<String>> finishedMacs = new MutableLiveData<>(new HashSet<>());
    private final MutableLiveData<List<Device>> processedDevicesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> savedStateLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> lastFinishedMac = new MutableLiveData<>();
    private final Map<String, Device> processedDevices = new HashMap<>();
    private final Set<String> selectedMacs = new HashSet<>();
    private final ResourceProvider resourceProvider;
    private MessageCallback messageCallback;

    @Inject
    public SensorServiceImpl(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void setSnackBarCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    @Override
    public LiveData<List<Device>> getScannedDevicesLiveData() {
        return processedDevicesLiveData;
    }

    @Override
    public void updateScannedDevices(List<Device> newDevices) {
        for (Device device : newDevices) {
            String mac = device.getDevice().getAddress();
            Device existing = processedDevices.get(mac);
            if (existing == null) {
                if (isMacSelected(mac)) {
                    device.setSelected(true);
                }
                processedDevices.put(mac, device);
            } else if (isMacSelected(mac)) {
                existing.setSelected(true);
            }
        }
        processedDevicesLiveData.setValue(new ArrayList<>(processedDevices.values()));
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void markMacAsSelected(Device device) {
        device.setSelected(true);
        String name = device.getDevice().getName();
        String mac = device.getDevice().getAddress();

        messageCallback.showMessage(resourceProvider.getString(selected, name));
        selectedMacs.add(mac);
    }

    @Override
    public void resetSelectedDevices() {
        selectedMacs.clear();

        for (Device device : processedDevices.values()) {
            device.setSelected(false);
        }

        processedDevicesLiveData.setValue(new ArrayList<>(processedDevices.values()));
    }

    @Override
    public void resetSelectedDevicesByMacs(Set<String> macs) {
        boolean changed = false;
        for (String mac : macs) {
            if (selectedMacs.remove(mac)) {
                Device device = processedDevices.get(mac);
                if (device != null) {
                    device.setSelected(false);
                    changed = true;
                }
            }
        }
        if (changed) {
            processedDevicesLiveData.setValue(new ArrayList<>(processedDevices.values()));
        }
    }

    @Override
    public boolean isMacSelected(String mac) {
        return selectedMacs.contains(mac);
    }

    @Override
    public LiveData<Boolean> getSavedStateLiveData() {
        return savedStateLiveData;
    }

    @Override
    public void markAsSaved() {
        savedStateLiveData.setValue(true);
    }

    @Override
    public void markAsUnsaved() {
        savedStateLiveData.setValue(false);
    }

    @Override
    public void clearMacs() {
        finishedMacs.setValue(new HashSet<>());
    }

    @Override
    public void addConfiguredMac(String mac) {
        if (mac != null && !mac.isEmpty()) {
            Set<String> value = finishedMacs.getValue();
            if (value != null) {
                Set<String> current = new HashSet<>(value);
                current.add(mac);
                finishedMacs.setValue(current);
            }
        }
    }

    @Override
    public void setLastConfiguredMac(String mac) {
        lastFinishedMac.setValue(mac);
    }

    @Override
    public LiveData<String> getLastConfiguredMac() {
        return lastFinishedMac;
    }

    @Override
    public LiveData<Set<String>> getConfiguredMacs() {
        return finishedMacs;
    }
}
