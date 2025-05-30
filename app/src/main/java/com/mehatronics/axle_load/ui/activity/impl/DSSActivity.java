package com.mehatronics.axle_load.ui.activity.impl;

import static com.mehatronics.axle_load.entities.enums.DeviceType.DSS;

import android.os.Bundle;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DSSActivity extends BaseBluetoothActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dss);
        setupBluetooth(DSS);
    }
}
