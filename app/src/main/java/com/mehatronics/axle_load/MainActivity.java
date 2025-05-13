package com.mehatronics.axle_load;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.navigation.ActivityNavigator;
import com.mehatronics.axle_load.security.permissions.observer.PermissionObserver;
import com.mehatronics.axle_load.security.permissions.usecase.PermissionUseCase;
import com.mehatronics.axle_load.viewModel.PermissionsViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    protected PermissionUseCase permissionUseCase;
    @Inject
    protected ActivityNavigator activityNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        var permissionsViewModel = new ViewModelProvider(this).get(PermissionsViewModel.class);
        var permissionObserver = new PermissionObserver(this, permissionUseCase, permissionsViewModel);

        permissionObserver.observePermissionsStatus(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityNavigator.registerActivities(this);
    }
}