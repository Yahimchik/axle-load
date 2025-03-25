package com.mehatronics.axle_load.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mehatronics.axle_load.R;

public class ConfigureFragment extends Fragment {
    private LinearLayout axisContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_configure, container, false);

        axisContainer = rootView.findViewById(R.id.axisContainer);

        Button btnAxis1 = rootView.findViewById(R.id.btn_axis_1);
        Button btnAxis2 = rootView.findViewById(R.id.btn_axis_2);
        Button btnAxis3 = rootView.findViewById(R.id.btn_axis_3);
        Button btnAxis4 = rootView.findViewById(R.id.btn_axis_4);
        Button btnAxis5 = rootView.findViewById(R.id.btn_axis_5);

        btnAxis1.setOnClickListener(v -> showAxes(1));
        btnAxis2.setOnClickListener(v -> showAxes(2));
        btnAxis3.setOnClickListener(v -> showAxes(3));
        btnAxis4.setOnClickListener(v -> showAxes(4));
        btnAxis5.setOnClickListener(v -> showAxes(5));

        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void showAxes(int numberOfAxes) {
        axisContainer.removeAllViews();
        for (int i = 1; i <= numberOfAxes; i++) {
            View axisView = LayoutInflater.from(getContext()).inflate(R.layout.layout_axis, axisContainer, false);
            TextView axisTitle = axisView.findViewById(R.id.axisTitle);
            axisTitle.setText("Ось " + i);

            ImageView axleImage = axisView.findViewById(R.id.axleImage);
            axleImage.setImageResource(R.drawable.axle_auto);

            axisContainer.addView(axisView);
        }
    }
}