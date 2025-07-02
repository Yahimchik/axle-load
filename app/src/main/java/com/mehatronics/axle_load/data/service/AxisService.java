package com.mehatronics.axle_load.data.service;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.entities.enums.ValidationError;

import java.util.List;
import java.util.Set;

public interface AxisService {
    LiveData<List<AxisModel>> getAxisList();

    LiveData<String> getMessage();

    void setDeviceToAxis(int axisNumber, AxisSide side, String mac);

    void resetDevicesForAxis(int axisNumber);

    String getMacForAxisSide(int axisNumber, AxisSide side);

    void onConfigureClicked(String input);

    LiveData<Event<InstalationPoint>> getAxisClick();

    void onWheelClicked(int axisNumber, AxisSide side);

    String getErrorMessage(ValidationError error);

    AxisModel cloneWithUpdatedDevice(AxisModel model, AxisSide sideToUpdate, String mac);

    Set<String> getMacsForAxis(int axisNumber);
}
