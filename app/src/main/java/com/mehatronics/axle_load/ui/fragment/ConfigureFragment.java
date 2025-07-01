package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.valueOf;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.domain.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.adapter.AxisAdapter;
import com.mehatronics.axle_load.ui.adapter.LoadingManager;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.notification.MessageCallback;
import com.mehatronics.axle_load.ui.viewModel.ConfigureViewModel;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;
import com.mehatronics.axle_load.ui.viewModel.SensorViewModel;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfigureFragment extends Fragment implements MessageCallback, BluetoothHandlerContract {
    @Inject
    protected FragmentNavigator fragmentNavigator;
    @Inject
    protected ResourceProvider provider;
    private EditText editTextAxisCount;
    private Button buttonConfigure;
    private Button saveButton;
    private SensorViewModel sensorViewModel;
    private ConfigureViewModel configureViewModel;
    private DeviceViewModel deviceViewModel;
    private RecyclerView recyclerView;
    private AxisAdapter adapter;
    private View root;
    private Set<String> list = new HashSet<>();
    private LoadingManager loadingManager;
    private BluetoothHandler handler;

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_configure, container, false);

        editTextAxisCount = root.findViewById(R.id.editTextAxisCount);
        buttonConfigure = root.findViewById(R.id.buttonConfigure);
        recyclerView = root.findViewById(R.id.recyclerViewAxes);
        saveButton = root.findViewById(R.id.buttonSave);

        configureViewModel = new ViewModelProvider(this).get(ConfigureViewModel.class);
        sensorViewModel = new ViewModelProvider(requireActivity()).get(SensorViewModel.class);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        handler = new BluetoothHandler(deviceViewModel, this, provider);
        loadingManager = new LoadingManager(root);

        setupRecyclerView();
        setupObservers();
        setupListeners();

        getValuesFromParentFragment();

        configureViewModel.getAxisList().observe(getViewLifecycleOwner(), list -> {
                    this.list = list.stream()
                            .flatMap(axis -> axis.getSideDeviceMap()
                                    .values()
                                    .stream())
                            .collect(Collectors.toSet()
                            );
                    Log.d("MyTag", String.valueOf(this.list));
                }
        );
        return root;
    }

    @Override
    public void showFragment() {
    }

    @Override
    public void loadingManagerShowLoading(boolean isLoading) {
        loadingManager.showLoading(isLoading);
    }

    @Override
    public void setIsAttemptingToConnect(boolean isAttempting) {
    }

    @Override
    public boolean isAttemptingToConnect() {
        return false;
    }

    @Override
    public void initConfigureButton() {
    }

    @Override
    public void onFragmentClosed() {
    }

    @Override
    public void onFragmentOpen() {
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving() || requireActivity().isFinishing()) {
            sensorViewModel.resetSelectedDevices();
        }
    }

    private void getValuesFromParentFragment() {
        getParentFragmentManager().setFragmentResultListener("selected_device_result", this, (key, bundle) -> {
            String mac = bundle.getString("mac");
            String sideStr = bundle.getString("axisSide");
            int axisNumber = bundle.getInt("axisNumber");
            configureViewModel.setDeviceToAxis(axisNumber, valueOf(sideStr), mac);
        });
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void setupRecyclerView() {
        adapter = new AxisAdapter(
                (axis, side) -> configureViewModel.onWheelClicked(axis, side),
                axis -> {
                    var macsToReset = configureViewModel.getMacsForAxis(axis);
                    configureViewModel.resetDevicesForAxis(axis);
                    sensorViewModel.resetSelectedDevicesByMacs(macsToReset);
                    adapter.setSavedState(false);
                },
                (axis, side) -> {
                    String mac = configureViewModel.getMacForAxisSide(axis, side);
                    if (mac != null) {
                        var device = findDevice(mac);
                        assert device != null;
                        handler.onDeviceSelected(device);
                    } else {
                        showMessage("Device not selected");
                    }
                }
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(v -> adapter.setSavedState(true));
    }

    private Device findDevice(String mac) {
        var devices = deviceViewModel.getScannedDevices().getValue();
        if (devices == null) return null;

        for (Device device : devices) {
            if (device.getDevice().getAddress().equalsIgnoreCase(mac)) {
                return device;
            }
        }
        return null;
    }

    private void setupObservers() {
        configureViewModel.getAxisList().observe(getViewLifecycleOwner(), adapter::submitList);
        configureViewModel.getMessage().observe(getViewLifecycleOwner(), this::showMessage);
        configureViewModel.getAxisClick().observe(getViewLifecycleOwner(), this::handleAxisClickEvent);

        deviceViewModel.getDeviceDetails().observe(getViewLifecycleOwner(), handler::handleDeviceDetails);
        deviceViewModel.isConnectedLiveData().observe(getViewLifecycleOwner(), handler::handleConnectionState);
    }

    private void handleAxisClickEvent(Event<InstalationPoint> event) {
        InstalationPoint data = event.getContentIfNotHandled();
        if (data != null) {
            fragmentNavigator.showFragment(AvailableSensorFragment.newInstance(data.getAxleNumber(), data.getPosition()));
        }
    }

    private void setupListeners() {
        buttonConfigure.setOnClickListener(v -> {
            String input = editTextAxisCount.getText().toString().trim();
            configureViewModel.onConfigureClicked(input);
        });
    }
}
