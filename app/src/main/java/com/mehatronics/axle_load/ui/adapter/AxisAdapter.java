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

import android.annotation.SuppressLint;
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
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.adapter.diffUtil.AxisDiffUtil;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisClickListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisConnectListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisResetListener;

import java.util.Map;

public class AxisAdapter extends ListAdapter<AxisModel, AxisAdapter.AxisViewHolder> {
    private final OnAxisClickListener clickListener;
    private final OnAxisResetListener resetListener;
    private final OnAxisConnectListener connectListener;
    private boolean isSavedState = false;

    public AxisAdapter(OnAxisClickListener clickListener,
                       OnAxisResetListener resetListener,
                       OnAxisConnectListener connectListener) {
        super(new AxisDiffUtil());
        this.clickListener = clickListener;
        this.resetListener = resetListener;
        this.connectListener = connectListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSavedState(boolean saved) {
        this.isSavedState = saved;
        notifyDataSetChanged();
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
            Map<AxisSide, String> macs = axis.getSideDeviceMap();

            boolean isLeftSelected = macs.get(LEFT) != null;
            boolean isRightSelected = macs.get(RIGHT) != null;
            boolean isCenterSelected = macs.get(CENTER) != null;

            boolean isLeftEnabled = !isLeftSelected && !isCenterSelected;
            boolean isRightEnabled = !isRightSelected && !isCenterSelected;
            boolean isCenterEnabled = !isCenterSelected && !isLeftSelected && !isRightSelected;

            updateSensorView(axleSensorLeft, axis, LEFT, isLeftSelected, isLeftEnabled);
            updateSensorView(axleSensorRight, axis, RIGHT, isRightSelected, isRightEnabled);
            updateSensorView(axleSensorCenter, axis, CENTER, isCenterSelected, isCenterEnabled);
        }

        private void updateSensorView(ImageView view, AxisModel axis, AxisSide side, boolean isSelected, boolean isEnabled) {
            setIcon(view, side, isSelected ? axis.getDeviceForSide(side) : null);
            view.setEnabled(isEnabled || (isSavedState && isSelected));
            view.setAlpha((isEnabled || (isSavedState && isSelected)) ? 1f : 0.3f);

            view.setOnClickListener(null);

            if (isSavedState && isSelected) {
                view.setOnClickListener(v -> connectListener.onConnect(axis.getNumber(), side));
            } else if (!isSavedState && isEnabled) {
                view.setOnClickListener(v -> clickListener.onClick(axis.getNumber(), side));
            }
            resetButton.setOnClickListener(v -> {
                resetListener.onReset(axis.getNumber());
                setSavedState(false);
            });
        }

        private void setIcon(ImageView imageView, AxisSide side, String mac) {
            if (mac != null) {
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
