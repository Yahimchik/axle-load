package com.mehatronics.axle_load.adapter.sensor;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.utils.diffUtil.SensorDiffCallback;

public class SensorAdapter extends ListAdapter<Device, SensorAdapter.SensorViewHolder> {
    private final OnSensorClickListener listener;

    public interface OnSensorClickListener {
        void onSensorClick(Device device);
    }

    public SensorAdapter(OnSensorClickListener listener) {
        super(new SensorDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Device device = getItem(position);
        boolean isSelected = device.isSelected();

        try {
            holder.sensorName.setText(device.getDevice().getName());
            holder.macAddress.setText(device.getDevice().getAddress());
        } catch (SecurityException e) {
            Log.e("MyTag", "Error accessing device details: " + e.getMessage());
        }

        updateViewState(holder, isSelected);

        if (!isSelected) {
            holder.itemView.setOnClickListener(v -> listener.onSensorClick(device));
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    private void updateViewState(SensorViewHolder holder, boolean isSelected) {
        holder.itemView.setAlpha(isSelected ? 0.5f : 1f);
        holder.itemView.setEnabled(!isSelected);
        holder.itemView.setClickable(!isSelected);

        int sensorNameColor = ContextCompat.getColor(holder.itemView.getContext(),
                isSelected ? R.color.button_selected_color : R.color.black);
        int macAddressColor = ContextCompat.getColor(holder.itemView.getContext(),
                isSelected ? R.color.button_selected_color : R.color.card_background_color);

        holder.sensorName.setTextColor(sensorNameColor);
        holder.macAddress.setTextColor(macAddressColor);
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView sensorName;
        TextView macAddress;

        SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            sensorName = itemView.findViewById(R.id.sensorName);
            macAddress = itemView.findViewById(R.id.macAddress);
        }
    }
}