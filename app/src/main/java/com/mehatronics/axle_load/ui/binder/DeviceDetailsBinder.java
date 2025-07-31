package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;
import static com.mehatronics.axle_load.ui.adapter.diffUtil.CalibrationDiffUtil.hasTableChanged;

import android.view.View;
import android.widget.Button;
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
    private Button finishButton;
    private final DeviceDetailsFormatter formatter;
    private final SensorConfigFormatter configFormatter;

    @Inject
    public DeviceDetailsBinder(DeviceDetailsFormatter formatter, SensorConfigFormatter configFormatter) {
        this.formatter = formatter;
        this.configFormatter = configFormatter;
    }

    public void init(View view, DeviceViewModel vm) {
        sensorConfigAdapter = new SensorConfigAdapter(view, configFormatter);
        sensorInfoAdapter = new SensorInfoAdapter(view, formatter);
        tableAdapter = new TableAdapter(vm::addPoint, vm::deletePoint);
        finishButton = view.findViewById(R.id.finishButton);
        initRecyclerView(view, R.id.calibrationRecyclerView, tableAdapter);
    }

    public void setVisibility(boolean isSelection) {
        finishButton.setVisibility(Boolean.TRUE.equals(isSelection) ? View.VISIBLE : View.GONE);
    }

    public void finishButtonOnClick(View.OnClickListener listener) {
        finishButton.setOnClickListener(listener);
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

    public void setupSaveButton(View.OnClickListener listener) {
        sensorConfigAdapter.setSaveClickListener(listener);
    }

    public void setupReadFromSensorButton(View.OnClickListener listener) {
        sensorInfoAdapter.setReadFromSensorButtonClickListener(listener);
    }

    public void setupSaveTableButton(View.OnClickListener listener) {
        sensorInfoAdapter.setSaveTableButton(listener);
    }

    public void updateSensorConfig(SensorConfig config) {
        sensorConfigAdapter.updateConfig(config);
    }
}

//@FragmentScoped
//public class DeviceDetailsBinder {
//    private final DeviceDetailsFormatter formatter;
//    private final SensorConfigFormatter configFormatter;
//    private final View root;
//    private final DeviceViewModel viewModel;
//
//    private SensorConfigAdapter sensorConfigAdapter;
//    private SensorInfoAdapter sensorInfoAdapter;
//    private TableAdapter tableAdapter;
//
//    private DeviceDetailsBinder(Builder builder) {
//        this.formatter = builder.formatter;
//        this.configFormatter = builder.configFormatter;
//        this.root = builder.root;
//        this.viewModel = builder.viewModel;
//
//        initAdapters();
//        setupRecyclerView();
//        if (builder.popupResetListener != null && builder.popupSetNewListener != null) {
//            setupPopupMenu(builder.popupResetListener, builder.popupSetNewListener);
//        }
//    }
//
//    private void initAdapters() {
//        sensorConfigAdapter = new SensorConfigAdapter(root, configFormatter);
//        sensorInfoAdapter = new SensorInfoAdapter(root, formatter);
//        tableAdapter = new TableAdapter(viewModel::addPoint, viewModel::deletePoint);
//    }
//
//    private void setupRecyclerView() {
//        initRecyclerView(root, R.id.calibrationRecyclerView, tableAdapter);
//    }
//
//    private void setupPopupMenu(View.OnClickListener resetClickListener, View.OnClickListener setNewClickListener) {
//        ImageButton overflowButton = root.findViewById(R.id.overflowButton);
//        overflowButton.setOnClickListener(v -> {
//            PopupMenu popup = new PopupMenu(v.getContext(), v);
//            popup.getMenuInflater().inflate(R.menu.device_menu, popup.getMenu());
//            popup.setOnMenuItemClickListener(item -> {
//                int itemId = item.getItemId();
//                if (itemId == R.id.menu_reset_password) {
//                    resetClickListener.onClick(v);
//                    return true;
//                } else if (itemId == R.id.menu_set_new_password) {
//                    setNewClickListener.onClick(v);
//                    return true;
//                }
//                return false;
//            });
//            popup.show();
//        });
//    }
//
//    public void bindInfo(DeviceDetails deviceDetails) {
//        sensorInfoAdapter.bind(deviceDetails);
//    }
//
//    public void bindTable(List<CalibrationTable> table) {
//        if (hasTableChanged(tableAdapter.getCurrentList(), table)) {
//            if (table.size() > 2) {
//                List<CalibrationTable> filteredList = table.subList(1, table.size() - 1);
//                tableAdapter.submitList(new ArrayList<>(filteredList));
//            }
//        }
//    }
//
//    public void bindConfigure(SensorConfig sensorConfig) {
//        sensorConfigAdapter.bind(sensorConfig);
//    }
//
//    public void setupSaveButton(View.OnClickListener listener) {
//        sensorConfigAdapter.setSaveClickListener(listener);
//    }
//
//    public void setupReadFromSensorButton(View.OnClickListener listener) {
//        sensorInfoAdapter.setReadFromSensorButtonClickListener(listener);
//    }
//
//    public void setupSaveTableButton(View.OnClickListener listener) {
//        sensorInfoAdapter.setSaveTableButton(listener);
//    }
//
//    public void updateSensorConfig(SensorConfig config) {
//        sensorConfigAdapter.updateConfig(config);
//    }
//
//    // ===== Builder =====
//
//    public static class Builder {
//        private final DeviceDetailsFormatter formatter;
//        private final SensorConfigFormatter configFormatter;
//
//        private View root;
//        private DeviceViewModel viewModel;
//        private View.OnClickListener popupResetListener;
//        private View.OnClickListener popupSetNewListener;
//
//        public Builder(DeviceDetailsFormatter formatter, SensorConfigFormatter configFormatter) {
//            this.formatter = formatter;
//            this.configFormatter = configFormatter;
//        }
//
//        public Builder withRoot(View root) {
//            this.root = root;
//            return this;
//        }
//
//        public Builder withViewModel(DeviceViewModel viewModel) {
//            this.viewModel = viewModel;
//            return this;
//        }
//
//        public Builder withPopupMenuListeners(View.OnClickListener resetListener, View.OnClickListener setNewListener) {
//            this.popupResetListener = resetListener;
//            this.popupSetNewListener = setNewListener;
//            return this;
//        }
//
//        public DeviceDetailsBinder build() {
//            if (root == null || viewModel == null) {
//                throw new IllegalStateException("DeviceDetailsBinder: required fields not set");
//            }
//            return new DeviceDetailsBinder(this);
//        }
//    }
//}
