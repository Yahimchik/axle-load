package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SensorViewModel extends ViewModel {
    private final List<MutableLiveData<String>> leftImages = new ArrayList<>();
    private final List<MutableLiveData<String>> centerImages = new ArrayList<>();
    private final List<MutableLiveData<String>> rightImages = new ArrayList<>();

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

    private MutableLiveData<String> getOrCreateImage(List<MutableLiveData<String>> list, int axis, String defaultValue) {
        while (list.size() <= axis) {
            list.add(new MutableLiveData<>(defaultValue));
        }
        return list.get(axis);
    }

    private void adjustListSize(List<MutableLiveData<String>> list, int newSize, String defaultValue) {
        while (list.size() < newSize) {
            list.add(new MutableLiveData<>(defaultValue));
        }
        while (list.size() > newSize) {
            list.remove(list.size() - 1);
        }
    }
}
