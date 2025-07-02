package com.mehatronics.axle_load.ui.viewModel;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.data.repository.DeviceRepository;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ConfigureViewModel extends ViewModel {
    private final DeviceRepository repository;

    @Inject
    public ConfigureViewModel(DeviceRepository repository) {
        this.repository = repository;
    }


    public LiveData<List<AxisModel>> getAxisList() {
        return repository.getAxisList();
    }

    public LiveData<String> getMessage() {
        return repository.getMessage();
    }

    public void setDeviceToAxis(int axisNumber, AxisSide side, String mac) {
        repository.setDeviceToAxis(axisNumber, side, mac);
    }

    public void resetDevicesForAxis(int axisNumber) {
        repository.resetDevicesForAxis(axisNumber);
    }

    public String getMacForAxisSide(int axisNumber, AxisSide side) {
        return repository.getMacForAxisSide(axisNumber, side);
    }

    public void onConfigureClicked(String input) {
        repository.onConfigureClicked(input);
    }

    public LiveData<Event<InstalationPoint>> getAxisClick() {
        return repository.getAxisClick();
    }

    public void onClick(int axisNumber, AxisSide side) {
        repository.onWheelClicked(axisNumber, side);
    }

    public Set<String> getMacsForAxis(int axisNumber) {
        return repository.getMacsForAxis(axisNumber);
    }

    public void setSnackBarCallback(MessageCallback messageCallback) {
        repository.setSnackBarCallback(messageCallback);
    }

    public LiveData<List<Device>> getScannedDevicesLiveData() {
        return repository.getScannedDevicesLiveData();
    }

    public void updateScannedDevices(List<Device> newDevices) {
        repository.updateScannedDevices(newDevices);
    }

    public void markMacAsSelected(Device device) {
        repository.markMacAsSelected(device);
    }

    public void resetSelectedDevices() {
        repository.resetSelectedDevices();
    }

    public void resetSelectedDevicesByMacs(Set<String> macs) {
        repository.resetSelectedDevicesByMacs(macs);
    }

    public void method(LifecycleOwner owner) {
        getAxisList().observe(owner, list
                -> Log.d("MyTag", String.valueOf(list.stream()
                .flatMap(axis -> axis.getSideDeviceMap()
                        .values()
                        .stream()
                ).collect(Collectors.toSet()))));
    }
}



