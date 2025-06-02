package com.mehatronics.axle_load.ui.adapter.diffUtil;

import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.domain.entities.AxisModel;

import java.util.List;

public class AxisDiffUtil extends DiffUtil.Callback {
    private final List<AxisModel> oldList;
    private final List<AxisModel> newList;

    public AxisDiffUtil(List<AxisModel> oldList, List<AxisModel> newList) {
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
        return oldList.get(oldItemPosition).number() == newList.get(newItemPosition).number();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
