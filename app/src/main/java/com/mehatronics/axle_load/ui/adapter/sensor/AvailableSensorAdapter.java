package com.mehatronics.axle_load.ui.adapter.sensor;

import static android.view.LayoutInflater.from;
import static com.mehatronics.axle_load.R.layout.item_sensor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.dto.DeviceResponseDTO;
import com.mehatronics.axle_load.ui.adapter.diffUtil.DeviceDiffUtil;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceClickListener;

import java.util.ArrayList;
import java.util.List;

public class AvailableSensorAdapter extends RecyclerView.Adapter<AvailableSensorAdapter.ViewHolder> {
    private final OnDeviceClickListener listener;
    private List<DeviceResponseDTO> devices = new ArrayList<>();

    public AvailableSensorAdapter(OnDeviceClickListener listener) {
        this.listener = listener;
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
        View view = from(parent.getContext()).inflate(item_sensor, parent, false);
        return new AvailableSensorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceResponseDTO device = devices.get(position);

        boolean isSelected = device.originalDevice().isSelected();

        holder.name.setText(device.name());
        holder.mac.setText(device.mac());
        holder.rssi.setText(device.rssi());

        updateViewState(holder, isSelected);

        if (!isSelected) {
            holder.itemView.setOnClickListener(v -> listener.onDeviceClick(device.originalDevice()));
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    private void updateViewState(ViewHolder holder, boolean isSelected) {
        holder.itemView.setAlpha(isSelected ? 0.5f : 1f);
        holder.itemView.setEnabled(!isSelected);
        holder.itemView.setClickable(!isSelected);

        int sensorNameColor = ContextCompat.getColor(holder.itemView.getContext(),
                isSelected ? R.color.button_selected_color : R.color.black);
        int macAddressColor = ContextCompat.getColor(holder.itemView.getContext(),
                isSelected ? R.color.button_selected_color : R.color.card_background_color);

        holder.name.setTextColor(sensorNameColor);
        holder.mac.setTextColor(macAddressColor);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView mac;
        TextView rssi;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.deviceName);
            mac = itemView.findViewById(R.id.deviceMacValue);
            rssi = itemView.findViewById(R.id.deviceRssiValue);
        }
    }
}
