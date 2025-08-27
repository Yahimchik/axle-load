package com.mehatronics.axle_load.ui.adapter.diffUtil;

import static android.Manifest.permission.BLUETOOTH_CONNECT;

import androidx.annotation.RequiresPermission;
import androidx.recyclerview.widget.DiffUtil;

import com.mehatronics.axle_load.data.dto.DeviceResponseDTO;

import java.util.List;

public class DeviceDiffUtil extends DiffUtil.Callback {
    private final List<DeviceResponseDTO> oldList;
    private final List<DeviceResponseDTO> newList;

    public DeviceDiffUtil(List<DeviceResponseDTO> oldList, List<DeviceResponseDTO> newList) {
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
        String oldAddress = oldList.get(oldItemPosition).mac();
        String newAddress = newList.get(newItemPosition).mac();
        return oldAddress.equals(newAddress);
    }

    @Override
    @RequiresPermission(BLUETOOTH_CONNECT)
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        DeviceResponseDTO oldDevice = oldList.get(oldItemPosition);
        DeviceResponseDTO newDevice = newList.get(newItemPosition);

        String oldName = oldDevice.name();
        String newName = newDevice.name();

        if (oldName == null) {
            if (newName != null) return false;
        } else {
            if (!oldName.equals(newName)) return false;
        }

        return oldDevice.equals(newDevice);
    }
}