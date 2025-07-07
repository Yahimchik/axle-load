package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.valueOf;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.Event;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.handler.BluetoothHandler;
import com.mehatronics.axle_load.domain.handler.BluetoothHandlerContract;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.ui.adapter.AxisViewBinder;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfigureFragment extends Fragment {
    private BluetoothHandlerContract contract;
    private DeviceViewModel viewModel;
    private BluetoothHandler handler;
    @Inject
    protected FragmentNavigator navigator;
    @Inject
    protected ResourceProvider provider;
    private AxisViewBinder binder;
    private View root;
    private LifecycleOwner owner;

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_configure, container, false);
        owner = getViewLifecycleOwner();

        setUpViewModels();
        setupObservers();
        getValuesFromParentFragment();

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof BluetoothHandlerContract) {
            contract = (BluetoothHandlerContract) context;
        } else {
            throw new ClassCastException("Activity must implement BluetoothHandlerContract");
        }

        if (context instanceof BaseBluetoothActivity) {
            handler = ((BaseBluetoothActivity) context).getBluetoothHandler();
        } else {
            throw new IllegalStateException("Activity must extend BaseBluetoothActivity");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving() || requireActivity().isFinishing()) {
            viewModel.resetSelectedDevices();
        }
    }

    private void setUpViewModels() {
        viewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);

        binder = new AxisViewBinder.builder()
                .withRoot(root)
                .onClick(handler::onClick)
                .onReset(handler::onReset)
                .onConnect(handler::onConnect)
                .onConfigureClicked(viewModel::onConfigureClicked)
                .build();

        viewModel.method(owner);
    }

    private void getValuesFromParentFragment() {
        getParentFragmentManager().setFragmentResultListener("selected_device_result", this, (key, bundle) -> {
            String mac = bundle.getString("mac");
            String sideStr = bundle.getString("axisSide");
            int axisNumber = bundle.getInt("axisNumber");
            viewModel.setDeviceToAxis(axisNumber, valueOf(sideStr), mac);
        });
    }

    private void setupObservers() {
        viewModel.getAxisList().observe(owner, binder::submitList);
        viewModel.getMessage().observe(owner, contract::showMessage);

        viewModel.getAxisClick().observe(owner, this::handleAxisClickEvent);
        viewModel.getSavedStateLiveData().observe(owner, binder::setSavedState);
    }

    private void handleAxisClickEvent(Event<InstalationPoint> event) {
        InstalationPoint data = event.getContentIfNotHandled();
        if (data != null) {
            navigator.showFragment(AvailableSensorFragment.newInstance(data));
        }
    }
}
