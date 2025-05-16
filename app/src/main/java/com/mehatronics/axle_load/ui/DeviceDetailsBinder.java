package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.TableAdapter;
import com.mehatronics.axle_load.adapter.SensorInfoAdapter;
import com.mehatronics.axle_load.adapter.SensorConfigAdapter;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

public class DeviceDetailsBinder {
    private final SensorConfigAdapter sensorConfigAdapter;
    private final SensorInfoAdapter sensorInfoAdapter;
    private final TableAdapter tableAdapter;

    public DeviceDetailsBinder(View view) {
        sensorConfigAdapter = new SensorConfigAdapter(view);
        sensorInfoAdapter = new SensorInfoAdapter(view);
        tableAdapter = new TableAdapter();

        initRecyclerView(view, R.id.calibrationRecyclerView, tableAdapter);
    }

    public void bindInfo(DeviceDetails deviceDetails) {
        sensorInfoAdapter.bind(deviceDetails);
    }

    public void bindtable(DeviceDetails deviceDetails) {
        tableAdapter.updateData(deviceDetails.getTable());
    }

    public void bindConfigure(SensorConfig sensorConfig) {
        sensorConfigAdapter.bind(sensorConfig);
    }

    public void setupSaveButton(View.OnClickListener listener) {
        sensorConfigAdapter.setSaveClickListener(listener);
    }

    public void updateSensorConfig(SensorConfig config) {
        sensorConfigAdapter.updateConfig(config);
    }
}
