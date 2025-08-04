package com.mehatronics.axle_load.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehatronics.axle_load.R;
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
    protected void createBinder(View view) {
        var binder = new AxisViewBinder(view, handler, this, provider, navigator);

        observe(viewModel.getAxisList(), binder::submitList);
        observe(viewModel.getAxisClick(), this::handleAxisClickEvent);

        observe(viewModel.getSavedStateLiveData(), binder::setSavedState);
        observe(viewModel.getFinishedMacs(), binder::addFinishedMac);

        observeDeviceSelection(viewModel::setDeviceToAxis);
        observe(viewModel.getAxisList(), binder::updateSaveButtonState);
        observe(viewModel.getAllDevicesSaved(), binder::setFinishButtonVisible);

        viewModel.method(getViewLifecycleOwner());
    }
}