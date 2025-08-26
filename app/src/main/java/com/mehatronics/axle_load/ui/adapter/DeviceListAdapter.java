package com.mehatronics.axle_load.ui.adapter;

import static android.view.View.GONE;
import static com.mehatronics.axle_load.constants.StringConstants.UNKNOWN;
import static com.mehatronics.axle_load.domain.entities.enums.DeviceType.BT_COM_MINI;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceClickListener;
import com.mehatronics.axle_load.data.dto.DeviceResponseDTO;
import com.mehatronics.axle_load.ui.adapter.diffUtil.DeviceDiffUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private final OnDeviceClickListener onDeviceClickListener;
    private List<DeviceResponseDTO> devices = new ArrayList<>();

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceResponseDTO device = devices.get(position);
        try {
            if (!Objects.equals(device.name(), UNKNOWN)) {
                holder.name.setText(device.name().trim());
                holder.mac.setText(device.mac());
                holder.rssi.setText(device.rssi());
                holder.weightValue.setText(device.weight());
                holder.pressureValue.setText(device.pressure());

                if (device.name().contains(BT_COM_MINI.toString())) {
                    holder.deviceIcon.setImageResource(R.drawable.ic_bt_com_mini);
                    holder.weightValue.setVisibility(GONE);
                    holder.pressureValue.setVisibility(GONE);
                    holder.deviceWeight.setVisibility(GONE);
                    holder.devicePressure.setVisibility(GONE);
                    holder.weightView.setVisibility(GONE);
                    holder.pressureView.setVisibility(GONE);
                }
            }

            holder.itemView.setOnClickListener(v -> onDeviceClickListener.onDeviceClick(device.originalDevice()));

        } catch (SecurityException e) {
            Log.d("MyTag", "Security exception: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView mac;
        TextView rssi;
        TextView weightValue;
        TextView pressureValue;
        TextView deviceWeight;
        TextView devicePressure;
        ImageView weightView;
        ImageView pressureView;
        ImageView deviceIcon;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.deviceName);
            mac = itemView.findViewById(R.id.deviceMacValue);
            rssi = itemView.findViewById(R.id.deviceRssiValue);
            weightValue = itemView.findViewById(R.id.deviceWeightValue);
            pressureValue = itemView.findViewById(R.id.devicePressureValue);
            deviceWeight = itemView.findViewById(R.id.deviceWeight);
            devicePressure = itemView.findViewById(R.id.devicePressure);
            weightView = itemView.findViewById(R.id.weightImage);
            pressureView = itemView.findViewById(R.id.pressureImage);
            deviceIcon = itemView.findViewById(R.id.deviceIcon);
        }
    }
}
