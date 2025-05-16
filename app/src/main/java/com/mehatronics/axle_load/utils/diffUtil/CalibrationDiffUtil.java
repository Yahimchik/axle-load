package com.mehatronics.axle_load.utils.diffUtil;

import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.entities.CalibrationTable;

import java.util.List;

public class CalibrationDiffUtil extends DiffUtil.Callback {
    private final List<CalibrationTable> oldList;
    private final List<CalibrationTable> newList;

    public CalibrationDiffUtil(List<CalibrationTable> oldList, List<CalibrationTable> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Уникальный идентификатор — например, детектор
        return oldList.get(oldItemPosition).getDetector() == newList.get(newItemPosition).getDetector();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        CalibrationTable oldItem = oldList.get(oldItemPosition);
        CalibrationTable newItem = newList.get(newItemPosition);
        return oldItem.getMultiplier() == newItem.getMultiplier();
    }
}
