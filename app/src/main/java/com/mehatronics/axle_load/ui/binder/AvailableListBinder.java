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

public class AvailableListBinder implements BaseBinder{
    private final AvailableSensorAdapter adapter;
    private final DeviceMapper deviceMapper;

    private AvailableListBinder(Builder builder) {
        this.deviceMapper = builder.deviceMapper;
        this.adapter = new AvailableSensorAdapter(builder.deviceClickListener);
        initRecyclerView(builder.root, R.id.sensorRecyclerView, adapter);
    }

    public void updateDevices(List<Device> devices) {
        adapter.setDevices(devices.stream()
                .map(deviceMapper::convertToDeviceDTO)
                .collect(Collectors.toList()));
    }

    public static class Builder {
        private View root;
        private DeviceMapper deviceMapper;
        private OnDeviceClickListener deviceClickListener;

        public Builder withRoot(View root) {
            this.root = root;
            return this;
        }

        public Builder withDeviceMapper(DeviceMapper mapper) {
            this.deviceMapper = mapper;
            return this;
        }

        public Builder withClickListener(OnDeviceClickListener listener) {
            this.deviceClickListener = listener;
            return this;
        }

        public AvailableListBinder build() {
            if (root == null || deviceMapper == null || deviceClickListener == null) {
                throw new IllegalStateException("AvailableListBinder: all fields must be set before building");
            }
            return new AvailableListBinder(this);
        }
    }
}
