package com.mehatronics.axle_load.utils.diffUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.entities.CalibrationTable;

import java.util.List;
import java.util.Objects;

public class CalibrationDiffUtil extends DiffUtil.ItemCallback<CalibrationTable> {
    public static boolean hasTableChanged(List<CalibrationTable> oldList, List<CalibrationTable> newList) {
        if (oldList == null || newList == null) return true;
        if (oldList.size() != newList.size()) return true;

        if (newList.size() < 2) return false;

        int index = newList.size() - 2;

        CalibrationTable oldItem = oldList.get(index);
        CalibrationTable newItem = newList.get(index);

        return !Objects.equals(oldItem.getDetector(), newItem.getDetector());
    }

    @Override
    public boolean areItemsTheSame(@NonNull CalibrationTable o, @NonNull CalibrationTable n) {
        return o.getId().equals(n.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull CalibrationTable o, @NonNull CalibrationTable n) {
        return o.getDetector() == n.getDetector()
                && o.getMultiplier() == n.getMultiplier()
                && o.isLast() == n.isLast();
    }
}

