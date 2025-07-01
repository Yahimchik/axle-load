package com.mehatronics.axle_load.ui.viewModel;

import static com.mehatronics.axle_load.R.string.selected;

import android.Manifest;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SensorViewModel extends ViewModel {
    private final MutableLiveData<List<Device>> processedDevicesLiveData = new MutableLiveData<>();
    private final Map<String, Device> processedDevices = new HashMap<>();
    private final Set<String> selectedMacs = new HashSet<>();
    private final ResourceProvider resourceProvider;
    private MessageCallback messageCallback;

    @Inject
    public SensorViewModel(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public void setSnackBarCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public LiveData<List<Device>> getScannedDevicesLiveData() {
        return processedDevicesLiveData;
    }

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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void markMacAsSelected(Device device) {
        String name = device.getDevice().getName();
        String mac = device.getDevice().getAddress();

        messageCallback.showMessage(resourceProvider.getString(selected, name));
        selectedMacs.add(mac);
    }

    public void resetSelectedDevices() {
        selectedMacs.clear();

        for (Device device : processedDevices.values()) {
            device.setSelected(false);
        }

        processedDevicesLiveData.setValue(new ArrayList<>(processedDevices.values()));
    }

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

    public Device findDeviceByMac(String mac) {
        List<Device> current = processedDevicesLiveData.getValue();
        if (current == null) return null;

        for (Device device : current) {
            if (device.getDevice().getAddress().equalsIgnoreCase(mac)) {
                return device;
            }
        }
        return null;
    }

    private boolean isMacSelected(String mac) {
        return selectedMacs.contains(mac);
    }
}