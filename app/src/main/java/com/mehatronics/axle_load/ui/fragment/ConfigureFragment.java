package com.mehatronics.axle_load.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.binder.AxisViewBinder;

import java.util.ArrayList;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfigureFragment extends BaseSensorFragment {
    private boolean firstLaunch = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configure, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving() || requireActivity().isFinishing()) {
            vm.resetSelectedDevices();
            vm.setLoadedAxisList(new ArrayList<>());
        }
    }

    @Override
    protected void createBinder(View view) {
        var binder = new AxisViewBinder(view,
                fileService,
                handler,
                this,
                provider,
                navigator,
                this::openFilePicker,
                repository,
                firstLaunch,
                vm::setDeviceInfoToSave,
                vm.getDeviceInfoToSave().getValue());
        firstLaunch = false;
        observe(vm.getAxisList(), binder::submitList);
        observe(vm.getAxisClick(), this::handleAxisClickEvent);

        observe(vm.getSavedStateLiveData(), binder::setSavedState);
        observe(vm.getFinishedMacs(), binder::addFinishedMac);

        observeDeviceSelection(vm::setDeviceToAxis);
        observe(vm.getAllDevicesSaved(), binder::setFinishButtonVisible);

        observe(vm.getAxisList(), axes -> {
            binder.submitList(new ArrayList<>(axes));
            binder.updateSaveButtonState(axes);
            binder.setAxisCount(axes.size());
        });

    }
}