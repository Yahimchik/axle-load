package com.mehatronics.axle_load.utils.diffUtil;

import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.entities.Device;

import java.util.List;

public class DeviceDiffUtil extends DiffUtil.Callback {
    private final List<Device> oldList;
    private final List<Device> newList;

    public DeviceDiffUtil(List<Device> oldList, List<Device> newList) {
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
        String oldAddress = oldList.get(oldItemPosition).getDevice().getAddress();
        String newAddress = newList.get(newItemPosition).getDevice().getAddress();
        return oldAddress.equals(newAddress);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Device oldDevice = oldList.get(oldItemPosition);
        Device newDevice = newList.get(newItemPosition);

        // Сравниваем только значимые поля (например, RSSI, данные и имя)
        return oldDevice.getDevice().getName().equals(newDevice.getDevice().getName()) &&
                oldDevice.getScanResult().getRssi() == newDevice.getScanResult().getRssi();
    }
}
