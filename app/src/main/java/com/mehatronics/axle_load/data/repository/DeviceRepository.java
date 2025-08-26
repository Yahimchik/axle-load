package com.mehatronics.axle_load.data.repository;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;

import java.util.List;
import java.util.Set;

public interface DeviceRepository {
    LiveData<List<AxisModel>> getAxisList();

    void setLoadedAxisList(List<AxisModel> list);

    LiveData<String> getMessage();

    void setDeviceToAxis(int axisNumber, AxisSide side, String mac);

    void resetDevicesForAxis(int axisNumber);

    String getMacForAxisSide(int axisNumber, AxisSide side);

    void onConfigureClicked(String input);

    LiveData<Event<InstalationPoint>> getAxisClick();

    void onWheelClicked(int axisNumber, AxisSide side);

    Set<String> getMacsForAxis(int axisNumber);

    void setDeviceName(String name);

    String getDeviceName();

    LiveData<List<Device>> getScannedDevicesLiveData();

    void updateScannedDevices(List<Device> newDevices);

    void markMacAsSelected(Device device);

    void resetSelectedDevices();

    void resetSelectedDevicesByMacs(Set<String> macs);

    LiveData<Boolean> getSavedStateLiveData();

    void markAsSaved();

    void markAsUnsaved();

    void clearMacs();

    LiveData<Set<String>> getConfiguredMacs();

    void addConfiguredMac(String mac);

    void setLastConfiguredMac(String mac);

    LiveData<String> getLastConfiguredMac();

    LiveData<Boolean> getSelectionModeLiveData();

    void setSelectionMode(boolean isSelection);

    void refreshScannedDevices();

    void setStateNumber(String stateNumber);

    String getStateNumber();
}