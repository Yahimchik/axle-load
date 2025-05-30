package com.mehatronics.axle_load.domain.viewModel;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.domain.usecase.PermissionUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PermissionsViewModel extends ViewModel {
    private final MutableLiveData<Boolean> bluetoothRequired = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> gpsRequired = new MutableLiveData<>(false);
    private final MutableLiveData<Void> requestPermissions = new MutableLiveData<>();
    private final PermissionUseCase permissionUseCase;

    @Inject
    public PermissionsViewModel(PermissionUseCase permissionUseCase) {
        this.permissionUseCase = permissionUseCase;
    }

    public void requestPermissions(Activity activity, int requestCode) {
        permissionUseCase.requestPermissions(activity, requestCode);
    }

    public LiveData<Boolean> isGpsRequired() {
        return gpsRequired;
    }

    public LiveData<Boolean> isBluetoothRequired() {
        return bluetoothRequired;
    }

    public LiveData<Void> getRequestPermissionsTrigger() {
        return requestPermissions;
    }

    public void checkPermissions() {
        requestPermissions.setValue(null);
        checkBluetoothStatus();
        checkGPSStatus();
    }

    private void checkBluetoothStatus() {
        bluetoothRequired.setValue(!permissionUseCase.isBluetoothEnabled());
    }

    private void checkGPSStatus() {
        gpsRequired.setValue(!permissionUseCase.isGpsEnabled());
    }
}

