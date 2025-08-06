package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;
import static com.mehatronics.axle_load.ui.adapter.diffUtil.CalibrationDiffUtil.hasTableChanged;

import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.PopupMenu;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.format.DeviceDetailsFormatter;
import com.mehatronics.axle_load.data.format.SensorConfigFormatter;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.ui.adapter.TableAdapter;
import com.mehatronics.axle_load.ui.adapter.sensor.SensorConfigAdapter;
import com.mehatronics.axle_load.ui.adapter.sensor.SensorInfoAdapter;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class DeviceDetailsBinder {
    private SensorConfigAdapter sensorConfigAdapter;
    private SensorInfoAdapter sensorInfoAdapter;
    private TableAdapter tableAdapter;
    private final DeviceDetailsFormatter formatter;
    private final SensorConfigFormatter configFormatter;

    @Inject
    public DeviceDetailsBinder(
            DeviceDetailsFormatter formatter,
            SensorConfigFormatter configFormatter) {
        this.configFormatter = configFormatter;
        this.formatter = formatter;
    }

    public void init(View view, DeviceViewModel vm) {
        sensorConfigAdapter = new SensorConfigAdapter(view, configFormatter);
        sensorInfoAdapter = new SensorInfoAdapter(view, formatter);
        tableAdapter = new TableAdapter(vm::addPoint, vm::deletePoint);
        initRecyclerView(view, R.id.calibrationRecyclerView, tableAdapter);
    }

    public void finishButtonOnClick(View.OnClickListener listener) {
        sensorInfoAdapter.finishButtonOnClick(listener);
    }

    public void setupPopupMenu(View view, View.OnClickListener resetClickListener, View.OnClickListener setNewClickListener) {
        ImageButton overflowButton = view.findViewById(R.id.overflowButton);
        overflowButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.device_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_reset_password) {
                    resetClickListener.onClick(v);
                    return true;
                } else if (itemId == R.id.menu_set_new_password) {
                    setNewClickListener.onClick(v);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    public void bindInfo(DeviceDetails deviceDetails) {
        sensorInfoAdapter.bind(deviceDetails);
    }

    public void bindTable(List<CalibrationTable> table) {
        if (hasTableChanged(tableAdapter.getCurrentList(), table)) {
            if (table.size() > 2) {
                List<CalibrationTable> filteredList = table.subList(1, table.size() - 1);
                tableAdapter.submitList(new ArrayList<>(filteredList));
            }
        }
    }

    public void bindConfigure(SensorConfig sensorConfig) {
        sensorConfigAdapter.bind(sensorConfig);
    }

    public void setupReadFromSensorButton(View.OnClickListener listener) {
        sensorInfoAdapter.setReadFromSensorButtonClickListener(listener);
    }

    public void updateSensorConfig(SensorConfig config) {
        sensorConfigAdapter.updateConfig(config);
    }
}