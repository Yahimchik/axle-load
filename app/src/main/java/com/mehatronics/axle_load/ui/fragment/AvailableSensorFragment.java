package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.READ;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.WAITING;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.mapper.DeviceMapper;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.ui.binder.AvailableListBinder;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AvailableSensorFragment extends BaseSensorFragment {
    @Inject
    protected DeviceMapper mapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_sensor, container, false);
    }

    @Override
    protected void createBinder(View view) {
        var binder = new AvailableListBinder(view, mapper, this::onSelected);
        if (repository.getCurrDeviceType().equals(DeviceType.BT_COM_MINI)){
            observe(vm.getBtComMiniDevices(), binder::updateDevices);
        }else {
            observe(vm.getScannedDevices(), vm::updateScannedDevices);
            observe(vm.getScannedDevicesLiveData(), binder::updateDevices);
        }
    }
}