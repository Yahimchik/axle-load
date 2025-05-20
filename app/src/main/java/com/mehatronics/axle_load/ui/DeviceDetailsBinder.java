package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;
import static com.mehatronics.axle_load.utils.diffUtil.CalibrationDiffUtil.hasTableChanged;

import android.util.Log;
import android.view.View;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.TableAdapter;
import com.mehatronics.axle_load.adapter.sensor.SensorConfigAdapter;
import com.mehatronics.axle_load.adapter.sensor.SensorInfoAdapter;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.utils.diffUtil.CalibrationDiffUtil;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;

import java.util.ArrayList;
import java.util.List;

public class DeviceDetailsBinder {
    private final SensorConfigAdapter sensorConfigAdapter;
    private final SensorInfoAdapter sensorInfoAdapter;
    private final TableAdapter tableAdapter;

    public DeviceDetailsBinder(View view, BluetoothViewModel vm) {
        sensorConfigAdapter = new SensorConfigAdapter(view);
        sensorInfoAdapter = new SensorInfoAdapter(view);
        tableAdapter = new TableAdapter(vm::addPoint, vm::deletePoint);

        initRecyclerView(view, R.id.calibrationRecyclerView, tableAdapter);
    }

    public void bindInfo(DeviceDetails deviceDetails) {
        sensorInfoAdapter.bind(deviceDetails);
    }

    public void bindTable(List<CalibrationTable> table) {
        if (hasTableChanged(tableAdapter.getCurrentList(), table)) {
            tableAdapter.submitList(new ArrayList<>(table));
        }
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
