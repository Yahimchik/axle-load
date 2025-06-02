package com.mehatronics.axle_load.ui.viewModel;

import android.Manifest;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SensorViewModel extends ViewModel {
    private final MutableLiveData<List<Device>> processedDevicesLiveData = new MutableLiveData<>();
    private final List<MutableLiveData<String>> centerImages = new ArrayList<>();
    private final List<MutableLiveData<String>> rightImages = new ArrayList<>();
    private final List<MutableLiveData<String>> leftImages = new ArrayList<>();
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
        messageCallback.showMessage(resourceProvider.getString(R.string.selected, device.getDevice().getName()));
        selectedMacs.add(device.getDevice().getAddress());
    }

    private boolean isMacSelected(String mac) {
        return selectedMacs.contains(mac);
    }
}