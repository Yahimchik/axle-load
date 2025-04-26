package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;
import static com.mehatronics.axle_load.utils.constants.ButtonsConstants.AXLES_BTN_IDS;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.AxisAdapter;
import com.mehatronics.axle_load.viewModel.ConfigureViewModel;
import com.mehatronics.axle_load.viewModel.SensorViewModel;

public class AxisListBinder {
    private final AxisAdapter axisAdapter;
    private final SensorViewModel sensorViewModel;

    public AxisListBinder(View view, ConfigureViewModel configureViewModel) {
        sensorViewModel = new ViewModelProvider((ViewModelStoreOwner) view.getContext()).get(SensorViewModel.class);
        axisAdapter = new AxisAdapter(0, sensorViewModel, (LifecycleOwner) view.getContext());
        initRecyclerView(view, R.id.axisContainer, axisAdapter);
        bindAxisButtons(view, configureViewModel);
    }

    private void bindAxisButtons(View view, ConfigureViewModel configureViewModel) {
        for (int i = 0; i < AXLES_BTN_IDS.length; ++i) {
            Button button = view.findViewById(AXLES_BTN_IDS[i]);
            final int num = i + 1;
            button.setOnClickListener(v -> configureViewModel.setNumberOfAxes(num));
        }
    }

    public void updateAxes(int numberOfAxes) {
        sensorViewModel.updateNumberOfAxes(numberOfAxes);
        axisAdapter.setNumberOfAxes(numberOfAxes);
    }
}
