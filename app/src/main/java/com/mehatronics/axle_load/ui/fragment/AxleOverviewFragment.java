package com.mehatronics.axle_load.ui.fragment;

import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToValue;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AxleOverviewFragment extends Fragment {

    private LinearLayout axisContainer;
    private LayoutInflater layoutInflater;
    private DeviceViewModel model;
    private final List<View> axleViews = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;
        model = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        return inflater.inflate(R.layout.fragment_axle_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        axisContainer = view.findViewById(R.id.axisContainer);
        inflateAxles(model.getAxisCount());
        model.getScannedDevices().observe(getViewLifecycleOwner(), this::updateAxisViews);
    }

    private void inflateAxles(int count) {
        axleViews.clear();
        axisContainer.removeAllViews();

        for (int i = 0; i < count; i++) {
            View axleView = layoutInflater.inflate(R.layout.axis_configured, axisContainer, false);
            axisContainer.addView(axleView);
            axleViews.add(axleView);
        }
    }

    private void updateAxisViews(List<Device> scannedDevices) {
        List<AxisModel> axisList = model.getAxisList().getValue();
        if (axisList == null) return;

        for (int i = 0; i < axisList.size() && i < axleViews.size(); i++) {
            AxisModel axis = axisList.get(i);
            View axleView = axleViews.get(i);

            TextView leftMacValue = axleView.findViewById(R.id.leftInfoMacValue);
            TextView leftWeightValue = axleView.findViewById(R.id.leftInfoWeightValue);
            TextView leftPressureValue = axleView.findViewById(R.id.leftInfoPressureValue);

            TextView rightMacValue = axleView.findViewById(R.id.rightInfoMacValue);
            TextView rightWeightValue = axleView.findViewById(R.id.rightInfoWeightValue);
            TextView rightPressureValue = axleView.findViewById(R.id.rightInfoPressureValue);

            ImageView leftIcon = axleView.findViewById(R.id.axleSensorLeft);
            ImageView rightIcon = axleView.findViewById(R.id.axleSensorRight);
            ImageView centerIcon = axleView.findViewById(R.id.axleSensorCenter);

            LinearLayout leftInfoContainer = axleView.findViewById(R.id.leftInfoContainer);
            LinearLayout rightInfoContainer = axleView.findViewById(R.id.rightInfoContainer);

            String leftMac = axis.getSideDeviceMap().get(AxisSide.LEFT);
            String rightMac = axis.getSideDeviceMap().get(AxisSide.RIGHT);
            String centerMac = axis.getSideDeviceMap().get(AxisSide.CENTER);

            leftIcon.setImageResource(leftMac != null ? R.drawable.axle_left_configured : R.drawable.axle_left);
            centerIcon.setImageResource(centerMac != null ? R.drawable.axle_center_configured : R.drawable.axle_center);
            rightIcon.setImageResource(rightMac != null ? R.drawable.axle_right_configured : R.drawable.axle_right);

            if (centerMac != null) {
                rightInfoContainer.setVisibility(View.VISIBLE);
                updateSensorInfo(centerMac, scannedDevices, rightMacValue, rightWeightValue, rightPressureValue);

                leftInfoContainer.setVisibility(View.INVISIBLE);
            } else {
                if (leftMac != null) {
                    leftInfoContainer.setVisibility(View.VISIBLE);
                    updateSensorInfo(leftMac, scannedDevices, leftMacValue, leftWeightValue, leftPressureValue);
                } else {
                    leftInfoContainer.setVisibility(View.INVISIBLE);
                }

                if (rightMac != null) {
                    rightInfoContainer.setVisibility(View.VISIBLE);
                    updateSensorInfo(rightMac, scannedDevices, rightMacValue, rightWeightValue, rightPressureValue);
                } else {
                    rightInfoContainer.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateSensorInfo(String mac, List<Device> scannedDevices, TextView macValue, TextView weightValue, TextView pressureValue) {
        macValue.setText(mac);
        for (Device device : scannedDevices) {
            if (mac.equals(device.getMacAddress())) {
                ScanResult scanResult = device.getScanResult();
                if (scanResult != null && scanResult.getScanRecord() != null) {
                    byte[] advertiseBytes = scanResult.getScanRecord().getBytes();
                    if (advertiseBytes != null && advertiseBytes.length >= 25) {
                        float weight = convertBytesToValue(advertiseBytes, 23, 24);
                        float pressure = convertBytesToValue(advertiseBytes, 21, 22) / 10f;

                        weightValue.setText(String.format("%.1f кг", weight));
                        pressureValue.setText(String.format("%.1f кПа", pressure));
                        return;
                    }
                }
            }
        }
        weightValue.setText("");
        pressureValue.setText("");
    }
}