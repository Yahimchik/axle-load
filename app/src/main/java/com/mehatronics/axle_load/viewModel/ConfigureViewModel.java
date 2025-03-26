package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConfigureViewModel extends ViewModel {
    private final MutableLiveData<Integer> numberOfAxes = new MutableLiveData<>();

    public LiveData<Integer> getNumberOfAxes() {
        return numberOfAxes;
    }

    public void setNumberOfAxes(int numberOfAxes) {
        this.numberOfAxes.setValue(numberOfAxes);
    }
}