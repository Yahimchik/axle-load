package com.mehatronics.axle_load.ui.binder;


import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.DeviceListAdapter;
import com.mehatronics.axle_load.adapter.listener.OnDeviceClickListener;
import com.mehatronics.axle_load.entities.Device;

import java.util.List;

public class DeviceListBinder {
    private final DeviceListAdapter deviceListAdapter;

    public DeviceListBinder(View view, OnDeviceClickListener listener) {
        deviceListAdapter = new DeviceListAdapter(listener);
        initRecyclerView(view,R.id.recyclerView,deviceListAdapter);
    }

    public void updateDevices(List<Device> devices) {
        deviceListAdapter.setDevices(devices);
    }
}

