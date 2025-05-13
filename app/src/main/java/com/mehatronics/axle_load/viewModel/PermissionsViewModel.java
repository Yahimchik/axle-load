package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.security.permissions.usecase.PermissionUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PermissionsViewModel extends ViewModel {
    private final PermissionUseCase permissionUseCase;
    private final MutableLiveData<Boolean> permissionsGranted = new MutableLiveData<>();
    private final MutableLiveData<Boolean> bluetoothRequired = new MutableLiveData<>();
    private final MutableLiveData<Boolean> gpsRequired = new MutableLiveData<>();

    @Inject
    public PermissionsViewModel(PermissionUseCase permissionUseCase) {
        this.permissionUseCase = permissionUseCase;
        checkPermissions();
    }

    public LiveData<Boolean> getPermissionsStatus() {
        return permissionsGranted;
    }

    public LiveData<Boolean> isGpsRequired() {
        return gpsRequired;
    }

    public LiveData<Boolean> isBluetoothRequired() {
        return bluetoothRequired;
    }

    public void checkPermissions() {
        permissionsGranted.setValue(true);
        checkBluetoothStatus();
        checkGPSStatus();
    }

    private void checkBluetoothStatus() {
        if (!permissionUseCase.isBluetoothEnabled()) {
            bluetoothRequired.setValue(true);
        }
    }

    private void checkGPSStatus() {
        if (!permissionUseCase.isGpsEnabled()) {
            gpsRequired.setValue(true);
        }
    }
}

