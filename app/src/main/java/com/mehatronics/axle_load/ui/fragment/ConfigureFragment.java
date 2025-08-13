package com.mehatronics.axle_load.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.binder.AxisViewBinder;

import java.util.ArrayList;

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
            viewModel.setLoadedAxisList(new ArrayList<>());
        }
    }

    @Override
    protected void createBinder(View view) {
        var binder = new AxisViewBinder(view, saveToFileService, handler, this, provider, navigator, this::openFilePicker);

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