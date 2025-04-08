package com.mehatronics.axle_load.ui;

import static com.mehatronics.axle_load.ui.RecyclerViewInitializer.initRecyclerView;

import android.view.View;
import android.widget.Button;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        initRecyclerView(view,R.id.axisContainer,axisAdapter);

        Button btnAxis1 = view.findViewById(R.id.btn_axis_1);
        Button btnAxis2 = view.findViewById(R.id.btn_axis_2);
        Button btnAxis3 = view.findViewById(R.id.btn_axis_3);
        Button btnAxis4 = view.findViewById(R.id.btn_axis_4);
        Button btnAxis5 = view.findViewById(R.id.btn_axis_5);

        btnAxis1.setOnClickListener(v -> configureViewModel.setNumberOfAxes(1));
        btnAxis2.setOnClickListener(v -> configureViewModel.setNumberOfAxes(2));
        btnAxis3.setOnClickListener(v -> configureViewModel.setNumberOfAxes(3));
        btnAxis4.setOnClickListener(v -> configureViewModel.setNumberOfAxes(4));
        btnAxis5.setOnClickListener(v -> configureViewModel.setNumberOfAxes(5));
    }

    public void updateAxes(int numberOfAxes) {
        sensorViewModel.updateNumberOfAxes(numberOfAxes);
        axisAdapter.setNumberOfAxes(numberOfAxes);
    }
}
