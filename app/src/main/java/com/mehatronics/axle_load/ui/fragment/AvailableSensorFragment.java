package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.domain.entities.enums.ScreenType.AVAILABLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.domain.entities.enums.ScreenType;
import com.mehatronics.axle_load.ui.binder.AvailableListBinder;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AvailableSensorFragment extends BaseSensorFragment {
    @Inject
    protected DeviceMapper mapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSnackBarCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_sensor, container, false);
    }

    @Override
    protected AvailableListBinder createBinder(View view) {
        return new AvailableListBinder(view, mapper, this::onSelected);
    }

    @Override
    protected ScreenType getScreenType() {
        return AVAILABLE;
    }
}