package com.mehatronics.axle_load.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.service.SaveToFileService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.binder.AxisViewBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfigureFragment extends BaseSensorFragment {
    @Inject
    protected ResourceProvider provider;
    @Inject
    protected SaveToFileService service;

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
        var binder = new AxisViewBinder(view, service, handler, this, provider, navigator);

        observe(viewModel.getAxisList(), binder::submitList);
        observe(viewModel.getAxisClick(), this::handleAxisClickEvent);

        observe(viewModel.getSavedStateLiveData(), binder::setSavedState);
        observe(viewModel.getFinishedMacs(), binder::addFinishedMac);

        observeDeviceSelection(viewModel::setDeviceToAxis);
        observe(viewModel.getAxisList(), binder::updateSaveButtonState);
        observe(viewModel.getAllDevicesSaved(), binder::setFinishButtonVisible);

        viewModel.method(getViewLifecycleOwner());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == navigator.getRequestCodePickFile() && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                List<AxisModel> loaded = service.loadAxisConfigurationFromUri(requireContext(), uri);
                if (loaded != null && !loaded.isEmpty()) {
                    viewModel.setLoadedAxisList(loaded);
                    loadingManager.showLoading(true);
                    waitUntilDevicesScanned(loaded);
                } else {
                    showMessage("Не удалось загрузить конфигурацию");
                }
            }
        }
    }

    private void waitUntilDevicesScanned(List<AxisModel> axisList) {
        var targetMacs = axisList.stream()
                .flatMap(model -> model.getSideDeviceMap().values().stream())
                .collect(Collectors.toSet());

        var liveData = viewModel.getScannedDevices();
        var observer = new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> scannedDevices) {
                if (scannedDevices == null) return;

                var foundMacs = scannedDevices.stream()
                        .map(device -> device.getDevice().getAddress())
                        .collect(Collectors.toSet());

                if (foundMacs.containsAll(targetMacs)) {
                    for (Device device : scannedDevices) {
                        String mac = device.getDevice().getAddress();
                        if (targetMacs.contains(mac)) {
                            viewModel.markMacAsSelected(device);
                        }
                    }

                    viewModel.refreshScannedDevices();

                    loadingManager.showLoading(false);
                    liveData.removeObserver(this);
                }
            }
        };

        liveData.observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.setLoadedAxisList(new ArrayList<>());
    }
}