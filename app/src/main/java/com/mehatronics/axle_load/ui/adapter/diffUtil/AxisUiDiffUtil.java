package com.mehatronics.axle_load.ui.adapter.diffUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.domain.entities.AxisUiModel;

import java.util.Objects;

public class AxisUiDiffUtil extends DiffUtil.ItemCallback<AxisUiModel>{
    @Override
    public boolean areItemsTheSame(@NonNull AxisUiModel oldItem, @NonNull AxisUiModel newItem) {
        return Objects.equals(oldItem.macLeft(), newItem.macLeft());
    }

    @Override
    public boolean areContentsTheSame(@NonNull AxisUiModel oldItem, @NonNull AxisUiModel newItem) {
        return oldItem.equals(newItem);
    }
}