package com.mehatronics.axle_load.domain.viewModel;

import static com.mehatronics.axle_load.R.string.error_axis_count_out_of_range;
import static com.mehatronics.axle_load.R.string.error_empty_axis_count;
import static com.mehatronics.axle_load.R.string.error_invalid_number;
import static com.mehatronics.axle_load.R.string.wheel_center;
import static com.mehatronics.axle_load.R.string.wheel_left;
import static com.mehatronics.axle_load.R.string.wheel_right;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.domain.usecase.ValidateAxisCountUseCase;
import com.mehatronics.axle_load.ValidationResult;
import com.mehatronics.axle_load.entities.AxisModel;
import com.mehatronics.axle_load.entities.enums.AxisSide;
import com.mehatronics.axle_load.entities.enums.ValidationError;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ConfigureViewModel extends ViewModel {

    private final MutableLiveData<List<AxisModel>> axisList = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final ValidateAxisCountUseCase validationUseCase;
    private final ResourceProvider resourceProvider;

    @Inject
    public ConfigureViewModel(ValidateAxisCountUseCase validationUseCase, ResourceProvider resourceProvider) {
        this.validationUseCase = validationUseCase;
        this.resourceProvider = resourceProvider;
    }

    public LiveData<List<AxisModel>> getAxisList() {
        return axisList;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void onConfigureClicked(String input) {
        ValidationResult result = validationUseCase.execute(input);
        if (result instanceof ValidationResult.Success) {
            int count = ((ValidationResult.Success) result).getCount();
            List<AxisModel> list = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                list.add(new AxisModel(i));
            }
            axisList.setValue(list);
        } else if (result instanceof ValidationResult.Error) {
            ValidationError error = ((ValidationResult.Error) result).getError();
            message.setValue(getErrorMessage(error));
        }
    }

    public void onWheelClicked(int axisNumber, AxisSide side) {
        String text = switch (side) {
            case LEFT -> resourceProvider.getString(wheel_left, axisNumber);
            case RIGHT -> resourceProvider.getString(wheel_right, axisNumber);
            case CENTER -> resourceProvider.getString(wheel_center, axisNumber);
        };
        message.setValue(text);
    }


    private String getErrorMessage(ValidationError error) {
        return switch (error) {
            case EMPTY_AXIS_COUNT -> resourceProvider.getString(error_empty_axis_count);
            case AXIS_COUNT_OUT_OF_RANGE -> resourceProvider.getString(error_axis_count_out_of_range);
            case INVALID_NUMBER -> resourceProvider.getString(error_invalid_number);
        };
    }
}



