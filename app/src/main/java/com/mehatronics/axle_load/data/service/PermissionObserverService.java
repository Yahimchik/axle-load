package com.mehatronics.axle_load.data.service;

import static com.mehatronics.axle_load.R.string.dialog_bluetooth_message;
import static com.mehatronics.axle_load.R.string.dialog_bluetooth_title;
import static com.mehatronics.axle_load.R.string.dialog_gps_message;
import static com.mehatronics.axle_load.R.string.dialog_gps_title;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;

import com.mehatronics.axle_load.ui.viewModel.PermissionsViewModel;

public interface PermissionObserverService {
    void setPermissionsViewModel(PermissionsViewModel permissionsViewModel);

    void setActivity(Activity activity);

    void startPermissionFlow();

    void observePermissionsStatus(LifecycleOwner owner);
}
