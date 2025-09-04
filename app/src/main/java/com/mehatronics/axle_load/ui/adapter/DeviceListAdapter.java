package com.mehatronics.axle_load.ui.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mehatronics.axle_load.constants.StringConstants.UNKNOWN;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.dto.DeviceResponseDTO;
import com.mehatronics.axle_load.ui.adapter.diffUtil.DeviceDiffUtil;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceClickListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private final OnDeviceClickListener onDeviceClickListener;
    private List<DeviceResponseDTO> devices = new ArrayList<>();
    private final Set<String> expandedItems = new HashSet<>();

    public DeviceListAdapter(OnDeviceClickListener onDeviceClickListener) {
        this.onDeviceClickListener = onDeviceClickListener;
    }

    public void setDevices(List<DeviceResponseDTO> newDevices) {
        DeviceDiffUtil diffCallback = new DeviceDiffUtil(this.devices, newDevices);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.devices = new ArrayList<>(newDevices);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceResponseDTO device = devices.get(position);
        try {
            if (!Objects.equals(device.name(), UNKNOWN)) {
                holder.type.setText(typerAndSerialNumber(device.name())[0].trim());
                holder.serialNumber.setText(typerAndSerialNumber(device.name())[1].trim());
                holder.mac.setText(device.mac());
                holder.rssi.setText(device.rssi());
                holder.weightValue.setText(device.weight());
                holder.pressureValue.setText(device.pressure());
                holder.deviceBatteryValue.setText(device.battery());

                if (device.name().contains(BT_COM_MINI.toString())) {
                    holder.weightValue.setVisibility(GONE);
                    holder.pressureValue.setVisibility(GONE);
                    holder.deviceWeight.setVisibility(GONE);
                    holder.devicePressure.setVisibility(GONE);
                    holder.infoContainer.setVisibility(GONE);
                    holder.containerBattery.setVisibility(GONE);
                }
            }

            boolean isExpanded = expandedItems.contains(device.mac());
            holder.infoContainer.setVisibility(isExpanded ? VISIBLE : GONE);

            holder.itemView.setOnClickListener(v -> {
                if (expandedItems.contains(device.mac())) {
                    expandedItems.remove(device.mac());
                    holder.infoContainer.setVisibility(GONE);
                } else {
                    expandedItems.add(device.mac());
                    holder.infoContainer.setVisibility(VISIBLE);
                }
            });

            holder.connect.setOnClickListener(v ->
                    onDeviceClickListener.onDeviceClick(device.originalDevice()));

        } catch (SecurityException e) {
            Log.d("MyTag", "Security exception: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    private String[] typerAndSerialNumber(String input) {
        if (input == null || !input.contains("SN:")) {
            return null;
        }
        String[] parts = input.split("SN:");
        if (parts.length < 2) {
            return null;
        }
        return parts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView serialNumber;
        TextView mac;
        TextView rssi;
        TextView weightValue;
        TextView pressureValue;
        TextView deviceWeight;
        TextView devicePressure;
        TextView deviceBatteryValue;
        MaterialButton connect;
        LinearLayout infoContainer;
        LinearLayout containerBattery;

        ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.deviceType);
            serialNumber = itemView.findViewById(R.id.deviceSerialValue);
            mac = itemView.findViewById(R.id.deviceMacValue);
            rssi = itemView.findViewById(R.id.deviceRssiValue);
            weightValue = itemView.findViewById(R.id.deviceWeightValue);
            pressureValue = itemView.findViewById(R.id.devicePressureValue);
            deviceWeight = itemView.findViewById(R.id.deviceWeight);
            devicePressure = itemView.findViewById(R.id.devicePressure);
            connect = itemView.findViewById(R.id.buttonConnect);
            infoContainer = itemView.findViewById(R.id.containerInfo);
            deviceBatteryValue = itemView.findViewById(R.id.deviceBatteryValue);
            containerBattery = itemView.findViewById(R.id.containerBattery);
        }
    }
}
