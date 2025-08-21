package com.mehatronics.axle_load.ui.activity.impl;

import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.READ;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;

import android.os.Bundle;
import android.util.Log;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DSSActivity extends BaseBluetoothActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dss);
        repository.setStatus(READ);
        viewModel.setSaveToMiniLive(false);
        setupBluetooth(BT_COM_MINI);
    }
}
