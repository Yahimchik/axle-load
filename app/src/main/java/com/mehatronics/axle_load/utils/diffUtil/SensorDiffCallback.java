package com.mehatronics.axle_load.utils.diffUtil;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.entities.Device;

public class SensorDiffCallback extends DiffUtil.ItemCallback<Device> {

    @Override
    public boolean areItemsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
        return oldItem.getDevice().getAddress().equals(newItem.getDevice().getAddress());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
        try {
            return oldItem.isSelected() == newItem.isSelected()
                    && oldItem.getDevice().getName().equals(newItem.getDevice().getName())
                    && oldItem.getDevice().getAddress().equals(newItem.getDevice().getAddress());
        } catch (SecurityException e) {
            Log.d("MyTag", "Error comparing devices: " + e.getMessage());
            return false;
        }
    }
}
