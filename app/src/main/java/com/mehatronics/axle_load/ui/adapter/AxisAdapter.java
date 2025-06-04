package com.mehatronics.axle_load.ui.adapter;

import static com.mehatronics.axle_load.R.drawable.axle_center;
import static com.mehatronics.axle_load.R.drawable.axle_left;
import static com.mehatronics.axle_load.R.drawable.axle_right;
import static com.mehatronics.axle_load.R.drawable.axle_sensor_center;
import static com.mehatronics.axle_load.R.drawable.axle_sensor_left;
import static com.mehatronics.axle_load.R.drawable.axle_sensor_right;
import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.CENTER;
import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.LEFT;
import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.RIGHT;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.adapter.diffUtil.AxisDiffUtil;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisClickListener;

public class AxisAdapter extends ListAdapter<AxisModel, AxisAdapter.AxisViewHolder> {
    private final OnAxisClickListener listener;

    public AxisAdapter(OnAxisClickListener listener) {
        super(new AxisDiffUtil());
        this.listener = listener;
    }

    @NonNull
    @Override
    public AxisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_axis, parent, false);
        return new AxisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AxisViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class AxisViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        Button resetButton;
        ImageView axleSensorLeft;
        ImageView axleSensorCenter;
        ImageView axleSensorRight;

        public AxisViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.axisTitleRight);
            resetButton = itemView.findViewById(R.id.buttonReset);
            axleSensorLeft = itemView.findViewById(R.id.axleSensorLeft);
            axleSensorCenter = itemView.findViewById(R.id.axleSensorCenter);
            axleSensorRight = itemView.findViewById(R.id.axleSensorRight);
        }

        public void bind(AxisModel axis) {
            title.setText(String.valueOf(axis.getNumber()));

            setIcon(axleSensorLeft, LEFT, axis.getDeviceForSide(LEFT));
            setIcon(axleSensorCenter, CENTER, axis.getDeviceForSide(CENTER));
            setIcon(axleSensorRight, RIGHT, axis.getDeviceForSide(RIGHT));

            axleSensorLeft.setOnClickListener(v -> listener.onClick(axis.getNumber(), LEFT));
            axleSensorCenter.setOnClickListener(v -> listener.onClick(axis.getNumber(), CENTER));
            axleSensorRight.setOnClickListener(v -> listener.onClick(axis.getNumber(), RIGHT));
        }

        private void setIcon(ImageView imageView, AxisSide side, Device device) {
            if (device != null) {
                switch (side) {
                    case LEFT -> imageView.setImageResource(axle_sensor_left);
                    case CENTER -> imageView.setImageResource(axle_sensor_center);
                    case RIGHT -> imageView.setImageResource(axle_sensor_right);
                }
            } else {
                switch (side) {
                    case LEFT -> imageView.setImageResource(axle_left);
                    case CENTER -> imageView.setImageResource(axle_center);
                    case RIGHT -> imageView.setImageResource(axle_right);
                }
            }
        }
    }
}
