package com.mehatronics.axle_load.ui.activity.impl;

import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.DPS;

import android.os.Bundle;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DPSActivity extends BaseBluetoothActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dps);
        setupBluetooth(DPS);
    }
}

