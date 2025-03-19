package com.mehatronics.axle_load.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.Device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private List<Device> devices = new ArrayList<>();
    private final OnDeviceClickListener onDeviceClickListener;

    public interface OnDeviceClickListener {
        void onDeviceClick(Device device);
    }

    public DeviceListAdapter(OnDeviceClickListener onDeviceClickListener) {
        this.onDeviceClickListener = onDeviceClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDevices(List<Device> newDevices) {
        this.devices = newDevices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device device = devices.get(position);
        BluetoothDevice bluetoothDevice = device.getDevice();
        ScanResult scanResult = device.getScanResult();
        try {
            byte[] bytes = Objects.requireNonNull(scanResult.getScanRecord()).getBytes();
            holder.name.setText(bluetoothDevice.getName() != null ? bluetoothDevice.getName() : "Unknown");
            holder.mac.setText(bluetoothDevice.getAddress());

            holder.rssi.setText("RSSI: " + scanResult.getRssi() + " dBm");

            holder.type.setText("Weight: " + (short) ((bytes[23] & 0xFF) * 256 + (bytes[24] & 0xFF)) + " Kg");
            holder.status.setText("Pressure: " + (float) ((bytes[21] & 0xFF) * 256 + (bytes[22] & 0xFF)) / 10 + " kPa");

        } catch (SecurityException e) {
            //
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView mac;
        TextView rssi;
        TextView type;
        TextView status;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.deviceName);
            mac = itemView.findViewById(R.id.deviceMac);
            rssi = itemView.findViewById(R.id.deviceRssi);
            type = itemView.findViewById(R.id.deviceType);
            status = itemView.findViewById(R.id.deviceStatus);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Device device = devices.get(position);
                    if (onDeviceClickListener != null) {
                        onDeviceClickListener.onDeviceClick(device);
                    }
                }
            });
        }
    }

    private String getDeviceType(int deviceType) {
        switch (deviceType) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "Classic";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return "BLE";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return "Dual Mode";
            default:
                return "Unknown";
        }
    }
}
