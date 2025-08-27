package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.R.string.calibration;
import static com.mehatronics.axle_load.R.string.tar_table;
import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;
import static com.mehatronics.axle_load.ui.adapter.diffUtil.CalibrationDiffUtil.hasTableChanged;

import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.PopupMenu;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.format.DeviceDetailsFormatter;
import com.mehatronics.axle_load.data.format.SensorConfigFormatter;
import com.mehatronics.axle_load.data.service.FileService;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.TableAdapter;
import com.mehatronics.axle_load.ui.adapter.sensor.SensorConfigAdapter;
import com.mehatronics.axle_load.ui.adapter.sensor.SensorConfigValidator;
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
    private final FileService service;
    private final ResourceProvider provider;
    private final SensorConfigValidator validator;
    private DeviceViewModel vm;

    @Inject
    public DeviceDetailsBinder(
            FileService service,
            DeviceDetailsFormatter formatter,
            SensorConfigFormatter configFormatter,
            ResourceProvider provider,
            SensorConfigValidator validator) {
        this.service = service;
        this.configFormatter = configFormatter;
        this.formatter = formatter;
        this.provider = provider;
        this.validator = validator;
    }

    public void init(View view, DeviceViewModel vm) {
        this.vm = vm;
        sensorConfigAdapter = new SensorConfigAdapter(view, configFormatter,  validator);
        sensorInfoAdapter = new SensorInfoAdapter(view, formatter);
        tableAdapter = new TableAdapter(vm::addPoint, vm::deletePoint);
        initRecyclerView(view, R.id.calibrationRecyclerView, tableAdapter);
        saveToFileOnClick();
    }

    public void finishButtonOnClick(View.OnClickListener listener) {
        sensorConfigAdapter.finishButtonOnClick(listener);
    }

    public void readFromFileOnClick(View.OnClickListener listener) {
        sensorInfoAdapter.readFromFileButton(listener);
    }

    public void saveToFileOnClick() {
        sensorInfoAdapter.saveToFileOnClick(v
                -> {
            String deviceName = vm.getDeviceName();
            List<CalibrationTable> copy = new ArrayList<>(tableAdapter.getCurrentList());
            copy.remove(copy.size() - 1);
            service.saveToFile(v, v.getContext(), provider.getString(calibration), provider.getString(tar_table, deviceName), copy);
        });
    }

    public void setupPopupMenu(View view, PopupMenu.OnMenuItemClickListener listener) {
        ImageButton overflowButton = view.findViewById(R.id.overflowButton);
        overflowButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.device_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(listener);
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