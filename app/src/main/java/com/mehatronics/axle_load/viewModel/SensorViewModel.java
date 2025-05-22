package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.notification.MessageCallback;

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
    private MessageCallback messageCallback;

    @Inject
    public SensorViewModel() {
    }

    public void setSnackBarCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
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

    public LiveData<List<Device>> getScannedDevicesLiveData() {
        return processedDevicesLiveData;
    }

    public void markMacAsSelected(String mac) {
        try {
            messageCallback.showMessage(
                    "Selected: " + Objects.requireNonNull(processedDevices.get(mac)).getDevice().getName());
        } catch (SecurityException e) {
            //
        }
        selectedMacs.add(mac);
    }

    public MutableLiveData<String> getSensorImage(int axis, String position) {
        switch (position) {
            case "left":
                return getOrCreateImage(leftImages, axis, "axle_left");
            case "center":
                return getOrCreateImage(centerImages, axis, "axle_center");
            case "right":
                return getOrCreateImage(rightImages, axis, "axle_right");
            default:
                throw new IllegalArgumentException("Invalid sensor position: " + position);
        }
    }

    public void setSensorImage(int axis, String position, String image) {
        getSensorImage(axis, position).setValue(image);
    }

    public void updateNumberOfAxes(int newCount) {
        adjustListSize(leftImages, newCount, "axle_left");
        adjustListSize(centerImages, newCount, "axle_center");
        adjustListSize(rightImages, newCount, "axle_right");
    }

    private boolean isMacSelected(String mac) {
        return selectedMacs.contains(mac);
    }

    private MutableLiveData<String> getOrCreateImage(List<MutableLiveData<String>> list, int axis, String defaultValue) {
        while (list.size() <= axis) {
            list.add(new MutableLiveData<>(defaultValue));
        }
        return list.get(axis);
    }

    private void adjustListSize(List<MutableLiveData<String>> list, int newSize, String defaultValue) {
        int currentSize = list.size();
        if (currentSize == newSize) return;

        if (currentSize < newSize) {
            for (int i = currentSize; i < newSize; i++) {
                list.add(new MutableLiveData<>(defaultValue));
            }
        } else {
            list.subList(newSize, currentSize).clear();
        }
    }

}
