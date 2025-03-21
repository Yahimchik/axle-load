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
import com.mehatronics.axle_load.ui.DeviceDetailsBinder;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeviceDetailsFragment extends Fragment {
    private BluetoothViewModel bluetoothViewModel;
    private DeviceDetailsBinder deviceDetailsBinder;

    public static DeviceDetailsFragment newInstance() {
        return new DeviceDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothViewModel = new ViewModelProvider(requireActivity()).get(BluetoothViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_details, container, false);

        deviceDetailsBinder = new DeviceDetailsBinder(view);

        bluetoothViewModel.getDeviceDetails().observe(getViewLifecycleOwner(), deviceDetails -> {
            if (deviceDetails != null) {
                deviceDetailsBinder.bind(deviceDetails);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        deviceDetailsBinder = null;
        bluetoothViewModel.disconnect();
        bluetoothViewModel.clearDetails();
        if (getActivity() instanceof BaseBluetoothActivity) {
            ((BaseBluetoothActivity) getActivity()).resetDeviceNavigatorState();
        }
        Log.d("MyTag", "Device details fragment is closed");
    }
}