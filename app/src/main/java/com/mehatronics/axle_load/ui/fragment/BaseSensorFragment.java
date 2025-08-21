package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.R.string.selected;
import static com.mehatronics.axle_load.constants.BundleKeys.AXIS_NUMBER;
import static com.mehatronics.axle_load.constants.BundleKeys.AXIS_SIDE;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.data.service.SaveToFileService;
import com.mehatronics.axle_load.data.service.SensorSelectionService;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.ui.adapter.LoadingManager;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceSelectionCallback;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.notification.SnackbarManager;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public abstract class BaseSensorFragment extends Fragment implements MessageCallback {
    @Inject
    protected SensorSelectionService service;
    @Inject
    protected FragmentNavigator navigator;
    @Inject
    protected SnackbarManager manager;
    @Inject
    protected SaveToFileService fileService;
    @Inject
    protected ResourceProvider provider;
    @Inject
    protected DeviceTypeRepository repository;
    protected LoadingManager loadingManager;
    protected ActivityResultLauncher<Intent> pickFileLauncher;

    protected DeviceViewModel vm;
    protected BluetoothHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            List<AxisModel> loadedList = fileService.loadListFromUri(requireContext(), uri, AxisModel.class);
                            if (loadedList != null && !loadedList.isEmpty()) {
                                vm.setLoadedAxisList(loadedList);
                                loadingManager.showLoading(true);
                                waitUntilDevicesScanned(loadedList);
                            } else {
                                showMessage("Не удалось загрузить конфигурацию");
                            }
                        }
                    }
                }
        );
        vm = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseBluetoothActivity)
            handler = ((BaseBluetoothActivity) context).getBluetoothHandler();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createBinder(view);
        loadingManager = new LoadingManager(view);
        observe(vm.getMessage(), this::showMessage);
    }

    @Override
    public void showMessage(String message) {
        manager.showMessage(requireView(), message);
    }

    protected <T> void observe(LiveData<T> liveData, Observer<T> observer) {
        liveData.observe(getOwner(), observer);
    }

    protected void observeDeviceSelection(OnDeviceSelectionCallback callback) {
        service.observeSelectedDevice(getParentFragmentManager(), getOwner(), callback);
    }

    protected int getAxisNumber() {
        return requireArguments().getInt(AXIS_NUMBER);
    }

    protected AxisSide getAxisSide() {
        return AxisSide.valueOf(requireArguments().getString(AXIS_SIDE));
    }

    protected void handleAxisClickEvent(Event<InstalationPoint> event) {
        InstalationPoint data = event.getContentIfNotHandled();
        if (data != null) {
            service.openSensorSelection(navigator, data, new AvailableSensorFragment());
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    protected void onSelected(Device device) {
        Log.d("MyTag", device.getDevice().getName());
        if (device.getDevice().getName().contains(BT_COM_MINI.toString())) {
            handler.onDeviceSelected(device);
//            vm.connectToDevice(device);
        } else {
            vm.markMacAsSelected(device);
            showMessage(getString(selected, device.getDevice().getName()));
            service.returnSelectedDevice(
                    getParentFragmentManager(),
                    requireActivity(),
                    getAxisNumber(),
                    getAxisSide(),
                    device
            );
        }
    }

    protected abstract void createBinder(View view);

    private LifecycleOwner getOwner() {
        return getViewLifecycleOwner();
    }

    private void waitUntilDevicesScanned(List<AxisModel> axisList) {
        var targetMacs = axisList.stream()
                .flatMap(model -> model.getSideDeviceMap().values().stream())
                .collect(Collectors.toSet());

        var liveData = vm.getScannedDevices();
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
                            vm.markMacAsSelected(device);
                        }
                    }

                    vm.refreshScannedDevices();

                    loadingManager.showLoading(false);
                    liveData.removeObserver(this);
                }
            }
        };

        liveData.observe(getViewLifecycleOwner(), observer);
    }

    public void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickFileLauncher.launch(intent);
    }
}