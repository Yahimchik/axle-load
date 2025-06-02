package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceClickListener;
import com.mehatronics.axle_load.ui.adapter.sensor.AvailableSensorAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class AvailableListBinder {
    private final AvailableSensorAdapter adapter;
    private final DeviceMapper deviceMapper;

    public AvailableListBinder(View view, DeviceMapper deviceMapper, OnDeviceClickListener listener) {
        this.deviceMapper = deviceMapper;
        adapter = new AvailableSensorAdapter(listener);
        initRecyclerView(view, R.id.sensorRecyclerView, adapter);
    }

    public void updateDevices(List<Device> devices) {
        adapter.setDevices(devices.stream()
                .map(deviceMapper::convertToDeviceDTO)
                .collect(Collectors.toList()));
    }
}
