package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;
import android.widget.TextView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisUiModel;
import com.mehatronics.axle_load.ui.adapter.ConfiguredAxisAdapter;

import java.util.List;

public class ConfiguredAxisViewBinder {
    private final ConfiguredAxisAdapter adapter;
    private final TextView totalWeightValue;
    private final TextView axisCountValue;
    private final TextView sensorCountValue;

    public ConfiguredAxisViewBinder(View root) {
        this.adapter = new ConfiguredAxisAdapter();
        this.totalWeightValue = root.findViewById(R.id.totalWeightValue);
        this.axisCountValue = root.findViewById(R.id.axisCountValue);
        this.sensorCountValue = root.findViewById(R.id.sensorCountValue);
        initRecyclerView(root, R.id.recyclerView, adapter);
    }

    public void submitList(List<AxisUiModel> uiModels) {
        adapter.submitList(uiModels);

        double total = 0;
        int sensorCount = 0;

        for (AxisUiModel model : uiModels) {
            total += model.getTotalWeight();
            if (model.isLeftConnected()) sensorCount++;
            if (model.isRightConnected()) sensorCount++;
            if (model.isCenterConnected()) sensorCount++;
        }
        updateTotalWeight(total);
        updateAxisCount(uiModels.size());
        updateSensorCount(sensorCount);
    }

    public void updateTotalWeight(double totalWeightKg) {
        if (totalWeightValue != null) {
            totalWeightValue.setText(String.valueOf(totalWeightKg));
        }
    }

    private void updateAxisCount(int axisCount) {
        if (axisCountValue != null) {
            axisCountValue.setText(String.valueOf(axisCount));
        }
    }

    private void updateSensorCount(int sensorCount) {
        if (sensorCountValue != null) {
            sensorCountValue.setText(String.valueOf(sensorCount));
        }
    }
}
