package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.domain.entities.enums.ScreenType.CONFIGURE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.enums.ScreenType;
import com.mehatronics.axle_load.ui.binder.AxisViewBinder;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfigureFragment extends BaseSensorFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configure, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving() || requireActivity().isFinishing()) {
            viewModel.resetSelectedDevices();
        }
    }

    @Override
    protected AxisViewBinder createBinder(View view) {
        return new AxisViewBinder(view, handler);
    }

    @Override
    protected ScreenType getScreenType() {
        return CONFIGURE;
    }
}