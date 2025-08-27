package com.mehatronics.axle_load.ui.adapter.diffUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.domain.entities.AxisModel;

public class AxisDiffUtil extends DiffUtil.ItemCallback<AxisModel> {
    @Override
    public boolean areItemsTheSame(@NonNull AxisModel oldItem, @NonNull AxisModel newItem) {
        return oldItem.getNumber() == newItem.getNumber();
    }

    @Override
    public boolean areContentsTheSame(@NonNull AxisModel oldItem, @NonNull AxisModel newItem) {
        return oldItem.equals(newItem);
    }
}