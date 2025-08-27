package com.mehatronics.axle_load.data.service;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;

import com.mehatronics.axle_load.ui.viewModel.PermissionsViewModel;

public interface PermissionObserverService {
    void setPermissionsViewModel(PermissionsViewModel permissionsViewModel);

    void setActivity(Activity activity);

    void startPermissionFlow();

    void observePermissionsStatus(LifecycleOwner owner);
}