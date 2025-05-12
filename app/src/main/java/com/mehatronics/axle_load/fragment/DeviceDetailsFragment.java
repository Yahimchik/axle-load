package com.mehatronics.axle_load.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.ui.DeviceDetailsBinderImpl;
import com.mehatronics.axle_load.utils.CalibrationTableManager;
import com.mehatronics.axle_load.viewModel.DeviceViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment {
    @Inject
    protected CalibrationTableManager calibrationTableManager;
    private DeviceDetailsBinderImpl detailsBinder;
    private DeviceViewModel deviceViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_details, container, false);

        detailsBinder = new DeviceDetailsBinderImpl(view, calibrationTableManager);
        deviceViewModel.getDeviceDetails().observe(getViewLifecycleOwner(), detailsBinder::bind);
        deviceViewModel.getSensorConfigure().observe(getViewLifecycleOwner(), detailsBinder::bindConfigure);

        detailsBinder.setupSaveButton(v -> deviceViewModel.saveSensorConfiguration());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detailsBinder = null;
        deviceViewModel.clearDetails();
        deviceViewModel.disconnect();
        if (getActivity() instanceof BaseBluetoothActivity) {
            ((BaseBluetoothActivity) getActivity()).resetDeviceNavigatorState();
        }
        Log.d("MyTag", "Device details fragment is closed");
    }
}