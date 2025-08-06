package com.mehatronics.axle_load.ui.adapter.diffUtil;

import android.Manifest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.domain.entities.device.Device;

public class SensorDiffCallback extends DiffUtil.ItemCallback<Device> {

    @Override
    public boolean areItemsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
        return oldItem.getDevice().getAddress().equals(newItem.getDevice().getAddress());
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public boolean areContentsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
        return oldItem.isSelected() == newItem.isSelected()
                && oldItem.getDevice().getName().equals(newItem.getDevice().getName())
                && oldItem.getDevice().getAddress().equals(newItem.getDevice().getAddress());
    }
}
