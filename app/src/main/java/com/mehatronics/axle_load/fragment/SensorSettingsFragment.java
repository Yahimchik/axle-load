package com.mehatronics.axle_load.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.SensorAdapter;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;
import com.mehatronics.axle_load.viewModel.SensorViewModel;

import java.util.ArrayList;
import java.util.List;

public class SensorSettingsFragment extends Fragment {
    private SensorViewModel sensorViewModel;
    private BluetoothViewModel bluetoothViewModel;
    private SensorAdapter sensorAdapter;
    private String sensorPosition;
    private int axisIndex;
    private final List<Device> deviceList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sensorPosition = getArguments().getString("sensorPosition");
            axisIndex = getArguments().getInt("axisIndex", 0);
        }
        sensorViewModel = new ViewModelProvider(requireActivity()).get(SensorViewModel.class);
        bluetoothViewModel = new ViewModelProvider(requireActivity()).get(BluetoothViewModel.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor_settings, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.sensorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        sensorAdapter = new SensorAdapter(deviceList, this::onSensorSelected);
        recyclerView.setAdapter(sensorAdapter);

        bluetoothViewModel.getScannedDevices().observe(getViewLifecycleOwner(), devices -> {
            deviceList.clear();
            deviceList.addAll(devices);
            sensorAdapter.notifyDataSetChanged();
//            Log.d("MyTag", deviceList.toString());
        });

        return rootView;
    }

    private void onSensorSelected(Device device) {
        try {
            Toast.makeText(getContext(), "Selected: " + device.getDevice().getName(), Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            //
        }
        sensorViewModel.setSensorImage(axisIndex, sensorPosition, "axle_sensor_" + sensorPosition);
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
