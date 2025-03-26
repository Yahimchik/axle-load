package com.mehatronics.axle_load.ui;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.AxisAdapter;
import com.mehatronics.axle_load.viewModel.ConfigureViewModel;

public class AxisListBinder {
    private final AxisAdapter axisAdapter;

    public AxisListBinder(View view, ConfigureViewModel configureViewModel) {
        RecyclerView recyclerView = view.findViewById(R.id.axisContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        axisAdapter = new AxisAdapter(0, (sensorPosition, axisPosition)
                -> Toast.makeText(view.getContext(), "Clicked on " + sensorPosition + " of axis " + (axisPosition + 1), Toast.LENGTH_SHORT).show());
        recyclerView.setAdapter(axisAdapter);

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
        axisAdapter.setNumberOfAxes(numberOfAxes);
    }
}
