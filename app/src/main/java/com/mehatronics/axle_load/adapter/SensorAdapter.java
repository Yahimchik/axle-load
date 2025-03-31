package com.mehatronics.axle_load.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.Device;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {
    private final List<Device> devices;
    private final OnSensorClickListener listener;

    public interface OnSensorClickListener {
        void onSensorClick(Device device);
    }

    public SensorAdapter(List<Device> devices, OnSensorClickListener listener) {
        this.devices = devices;
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
        Device device = devices.get(position);
        try {
            holder.sensorName.setText(device.getDevice().getName());
            holder.macAddress.setText(device.getDevice().getAddress());
        } catch (SecurityException e) {
            //
        }
        holder.itemView.setOnClickListener(v -> listener.onSensorClick(device));
    }

    @Override
    public int getItemCount() {
        return devices.size();
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
