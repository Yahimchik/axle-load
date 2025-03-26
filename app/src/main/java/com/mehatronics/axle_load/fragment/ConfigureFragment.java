package com.mehatronics.axle_load.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.AxisListBinder;
import com.mehatronics.axle_load.viewModel.ConfigureViewModel;

public class ConfigureFragment extends Fragment {
    private ConfigureViewModel configureViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel = new ViewModelProvider(this).get(ConfigureViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_configure, container, false);
        var axisListBinder = new AxisListBinder(rootView, configureViewModel);

        configureViewModel.getNumberOfAxes().observe(getViewLifecycleOwner(), axisListBinder::updateAxes);
        return rootView;
    }
}