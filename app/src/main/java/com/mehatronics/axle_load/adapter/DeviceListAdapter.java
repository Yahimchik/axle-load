package com.mehatronics.axle_load.adapter;

import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToValue;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.listener.OnDeviceClickListener;
import com.mehatronics.axle_load.entities.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private final OnDeviceClickListener onDeviceClickListener;
    private List<Device> devices = new ArrayList<>();

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

        if (bluetoothDevice == null || scanResult == null || scanResult.getScanRecord() == null) {
            Log.w("MyTag", "Device or scan result is null at position: " + position);
            return;
        }

        byte[] bytes = scanResult.getScanRecord().getBytes();

        try {
            String deviceName = bluetoothDevice.getName() != null ? bluetoothDevice.getName() : "Unknown";
            String macAddress = bluetoothDevice.getAddress();
            int rssiValue = scanResult.getRssi();
            float weight = convertBytesToValue(bytes, 23, 24);
            float pressure = convertBytesToValue(bytes, 21, 22) / 10f;

            holder.name.setText(deviceName);
            holder.mac.setText(macAddress);
            holder.rssi.setText("RSSI: " + rssiValue + " dBm");
            holder.type.setText("Weight: " + weight + " Kg");
            holder.status.setText("Pressure: " + pressure + " kPa");

        } catch (SecurityException e) {
            Log.d("MyTag", "Security exception: " + e.getMessage());
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
}
