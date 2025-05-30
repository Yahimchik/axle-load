package com.mehatronics.axle_load.ui.binder;


import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.DeviceListAdapter;
import com.mehatronics.axle_load.adapter.listener.OnDeviceClickListener;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.mapper.DeviceMapper;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceListBinder {
    private final DeviceListAdapter deviceListAdapter;
    private final DeviceMapper deviceMapper;

    public DeviceListBinder(View view, OnDeviceClickListener listener, DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
        deviceListAdapter = new DeviceListAdapter(listener);
        initRecyclerView(view, R.id.recyclerView, deviceListAdapter);
    }

    public void updateDevices(List<Device> devices) {
        deviceListAdapter.setDevices(devices.stream()
                .map(deviceMapper::convertToDeviceDTO)
                .collect(Collectors.toList()));
    }
}

