package com.mehatronics.axle_load.utils.diffUtil;

import static android.Manifest.permission.BLUETOOTH_CONNECT;

import android.Manifest;

import androidx.annotation.RequiresPermission;
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
    @RequiresPermission(BLUETOOTH_CONNECT)
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Device oldDevice = oldList.get(oldItemPosition);
        Device newDevice = newList.get(newItemPosition);

        if (oldDevice.getDevice() == null || newDevice.getDevice() == null) return false;

        String oldName = oldDevice.getDevice().getName();
        String newName = newDevice.getDevice().getName();

        if (oldName == null) {
            if (newName != null) return false;
        } else {
            if (!oldName.equals(newName)) return false;
        }

        if (oldDevice.getScanResult() == null || newDevice.getScanResult() == null) return false;

        return oldDevice.getScanResult().getRssi() == newDevice.getScanResult().getRssi();
    }

}
