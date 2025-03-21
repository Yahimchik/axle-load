package com.mehatronics.axle_load.ui;


import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.DeviceListAdapter;
import com.mehatronics.axle_load.adapter.OnDeviceClickListener;
import com.mehatronics.axle_load.entities.Device;

import java.util.List;

public class DeviceListBinder {
    private final DeviceListAdapter deviceListAdapter;

    public DeviceListBinder(View view, OnDeviceClickListener listener) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        deviceListAdapter = new DeviceListAdapter(listener);
        recyclerView.setAdapter(deviceListAdapter);
    }

    public void updateDevices(List<Device> devices) {
        deviceListAdapter.setDevices(devices);
    }
}

