package com.mehatronics.axle_load.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.binder.ConfiguredAxisViewBinder;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AxleOverviewFragment extends Fragment {
    private DeviceViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        return inflater.inflate(R.layout.fragment_axle_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var binder = new ConfiguredAxisViewBinder(view);
        vm.getUiAxisModels().observe(getViewLifecycleOwner(), binder::submitList);
    }
}