package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.R.string.error_axis_out_of_range;
import static com.mehatronics.axle_load.R.string.error_empty_axis_count;
import static com.mehatronics.axle_load.R.string.error_invalid_number;
import static com.mehatronics.axle_load.R.string.wheel_center;
import static com.mehatronics.axle_load.R.string.wheel_left;
import static com.mehatronics.axle_load.R.string.wheel_right;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.data.service.AxisService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.ValidationResult;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.entities.enums.ValidationError;
import com.mehatronics.axle_load.domain.usecase.ValidateAxisCountUseCase;
import com.mehatronics.axle_load.localization.ResourceProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class AxisServiceImpl implements AxisService {
    private final MutableLiveData<Event<InstalationPoint>> axisClickEvent = new MutableLiveData<>();
    private final MutableLiveData<List<AxisModel>> axisList = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final ValidateAxisCountUseCase validationUseCase;
    private final ResourceProvider resourceProvider;

    @Inject
    public AxisServiceImpl(ValidateAxisCountUseCase validationUseCase, ResourceProvider resourceProvider) {
        this.validationUseCase = validationUseCase;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public LiveData<List<AxisModel>> getAxisList() {
        return axisList;
    }

    @Override
    public int getAxisCount() {
        return Objects.requireNonNull(axisList.getValue()).size();
    }

    @Override
    public LiveData<String> getMessage() {
        return message;
    }

    @Override
    public void setDeviceToAxis(int axisNumber, AxisSide side, String mac) {
        List<AxisModel> currentList = axisList.getValue();
        if (currentList == null) return;

        List<AxisModel> updatedList = new ArrayList<>();
        boolean updated = false;

        for (AxisModel model : currentList) {
            if (model.getNumber() == axisNumber) {
                AxisModel updatedModel = cloneWithUpdatedDevice(model, side, mac);
                updatedList.add(updatedModel);
                updated = true;
            } else {
                updatedList.add(model);
            }
        }

        if (!updated) {
            AxisModel newModel = new AxisModel(axisNumber);
            newModel.setDeviceForSide(side, mac);
            updatedList.add(newModel);
        }

        axisList.setValue(updatedList);
    }

    @Override
    public void resetDevicesForAxis(int axisNumber) {
        List<AxisModel> currentList = axisList.getValue();
        if (currentList == null) return;

        List<AxisModel> updatedList = new ArrayList<>();
        for (AxisModel model : currentList) {
            if (model.getNumber() == axisNumber) {
                AxisModel resetModel = new AxisModel(axisNumber);
                updatedList.add(resetModel);
            } else {
                updatedList.add(model);
            }
        }
        axisList.setValue(updatedList);
    }

    @Override
    public String getMacForAxisSide(int axisNumber, AxisSide side) {
        List<AxisModel> currentList = axisList.getValue();
        if (currentList == null) return null;

        for (AxisModel model : currentList) {
            if (model.getNumber() == axisNumber) {
                return model.getDeviceForSide(side);
            }
        }
        return null;
    }

    @Override
    public void onConfigureClicked(String input) {
        ValidationResult result = validationUseCase.execute(input);

        if (result instanceof ValidationResult.Success) {
            int count = ((ValidationResult.Success) result).getCount();

            List<AxisModel> currentList = axisList.getValue();
            if (currentList == null) {
                currentList = new ArrayList<>();
            } else {
                currentList = new ArrayList<>(currentList);
            }

            int currentSize = currentList.size();

            if (count > currentSize) {
                for (int i = currentSize + 1; i <= count; i++) {
                    currentList.add(new AxisModel(i));
                }
            } else if (count < currentSize) {
                currentList.subList(count, currentSize).clear();
            } else {
                return;
            }

            axisList.setValue(currentList);

        } else if (result instanceof ValidationResult.Error) {
            ValidationError error = ((ValidationResult.Error) result).getError();
            message.setValue(getErrorMessage(error));
        }
    }

    @Override
    public LiveData<Event<InstalationPoint>> getAxisClick() {
        return axisClickEvent;
    }

    @Override
    public void onWheelClicked(int axisNumber, AxisSide side) {
        switch (side) {
            case LEFT -> resourceProvider.getString(wheel_left, axisNumber);
            case RIGHT -> resourceProvider.getString(wheel_right, axisNumber);
            case CENTER -> resourceProvider.getString(wheel_center, axisNumber);
        }
        axisClickEvent.setValue(new Event<>(new InstalationPoint(axisNumber, side)));
    }

    @Override
    public String getErrorMessage(ValidationError error) {
        return switch (error) {
            case EMPTY_AXIS -> resourceProvider.getString(error_empty_axis_count);
            case AXIS_OUT_OF_RANGE -> resourceProvider.getString(error_axis_out_of_range);
            case INVALID_NUMBER -> resourceProvider.getString(error_invalid_number);
        };
    }

    @Override
    public AxisModel cloneWithUpdatedDevice(AxisModel model, AxisSide sideToUpdate, String mac) {
        AxisModel clone = new AxisModel(model.getNumber());
        for (AxisSide side : AxisSide.values()) {
            String existingMac = model.getDeviceForSide(side);
            if (existingMac != null) {
                clone.setDeviceForSide(side, existingMac);
            }
        }
        clone.setDeviceForSide(sideToUpdate, mac);
        return clone;
    }

    @Override
    public Set<String> getMacsForAxis(int axisNumber) {
        Set<String> macs = new HashSet<>();
        List<AxisModel> currentList = axisList.getValue();
        if (currentList == null) return macs;

        for (AxisModel model : currentList) {
            if (model.getNumber() == axisNumber) {
                macs.addAll(model.getSideDeviceMap().values().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
                break;
            }
        }
        return macs;
    }
}
