package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.ValidateAxisCountUseCase;
import com.mehatronics.axle_load.ValidationResult;
import com.mehatronics.axle_load.entities.AxisModel;
import com.mehatronics.axle_load.entities.enums.AxisSide;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ConfigureViewModel extends ViewModel {

    private final MutableLiveData<List<AxisModel>> axisList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final ValidateAxisCountUseCase validationUseCase;

    @Inject
    public ConfigureViewModel(ValidateAxisCountUseCase validationUseCase) {
        this.validationUseCase = validationUseCase;
    }

    public LiveData<List<AxisModel>> getAxisList() {
        return axisList;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
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
            String message = ((ValidationResult.Error) result).getMessage();
            errorMessage.setValue(message);
        }
    }

    public void onWheelClicked(int axisNumber, AxisSide side) {
        switch (side) {
            case LEFT:
                errorMessage.setValue("Левое колесо оси " + axisNumber);
                break;
            case RIGHT:
                errorMessage.setValue("Правое колесо оси " + axisNumber);
                break;
            case CENTER:
                errorMessage.setValue("Центр оси " + axisNumber);
                break;
        }
    }
}


