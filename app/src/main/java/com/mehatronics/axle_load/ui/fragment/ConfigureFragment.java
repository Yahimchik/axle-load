package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.domain.entities.enums.ScreenType.CONFIGURE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.enums.ScreenType;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.binder.AxisViewBinder;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfigureFragment extends BaseSensorFragment {
    @Inject
    protected ResourceProvider provider;

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
        return new AxisViewBinder.builder()
                .withRoot(view)
                .onAction(handler)
                .withMessageCallback(this)
                .withResourceProvider(provider)
                .build();
    }

    @Override
    protected ScreenType getScreenType() {
        return CONFIGURE;
    }
}