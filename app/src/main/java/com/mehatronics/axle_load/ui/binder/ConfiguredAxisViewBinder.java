package com.mehatronics.axle_load.ui.binder;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisUiModel;
import com.mehatronics.axle_load.ui.adapter.ConfiguredAxisAdapter;

import java.util.List;

public class ConfiguredAxisViewBinder {
    private final ConfiguredAxisAdapter adapter;

    public ConfiguredAxisViewBinder(View root) {
        this.adapter = new ConfiguredAxisAdapter();
        initRecyclerView(root, R.id.recyclerView, adapter);
    }

    public void submitList(List<AxisUiModel> uiModels) {
        adapter.submitList(uiModels);
    }
}
