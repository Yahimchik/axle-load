package com.mehatronics.axle_load.ui.adapter;

import static com.mehatronics.axle_load.R.drawable.ic_center;
import static com.mehatronics.axle_load.R.drawable.ic_center_saved;
import static com.mehatronics.axle_load.R.drawable.ic_center_selected;
import static com.mehatronics.axle_load.R.drawable.ic_left;
import static com.mehatronics.axle_load.R.drawable.ic_left_saved;
import static com.mehatronics.axle_load.R.drawable.ic_legt_selected;
import static com.mehatronics.axle_load.R.drawable.ic_right;
import static com.mehatronics.axle_load.R.drawable.ic_right_saved;
import static com.mehatronics.axle_load.R.drawable.ic_right_selected;
import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.CENTER;
import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.LEFT;
import static com.mehatronics.axle_load.domain.entities.enums.AxisSide.RIGHT;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.adapter.diffUtil.AxisDiffUtil;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisClickListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnAxisResetListener;
import com.mehatronics.axle_load.ui.binder.MainActivityBinder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AxisAdapter extends ListAdapter<AxisModel, AxisAdapter.AxisViewHolder> {
    private final OnAxisClickListener clickListener;
    private final OnAxisResetListener resetListener;
    private final Set<String> finishedMacs = new HashSet<>();
    private boolean isSavedState = false;

    public AxisAdapter(OnAxisClickListener clickListener,
                       OnAxisResetListener resetListener) {
        super(new AxisDiffUtil());
        this.clickListener = clickListener;
        this.resetListener = resetListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSavedState(boolean saved) {
        this.isSavedState = saved;
        notifyDataSetChanged();
    }

    public void setFinishedMacs(Set<String> macs) {
        this.finishedMacs.clear();
        if (macs != null) finishedMacs.addAll(macs);
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
        MaterialCardView resetButton;
        ImageView axleSensorLeft;
        ImageView axleSensorCenter;
        ImageView axleSensorRight;

        @SuppressLint("ClickableViewAccessibility")
        public AxisViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.axisTitleRight);
            resetButton = itemView.findViewById(R.id.resetCardView);
            axleSensorLeft = itemView.findViewById(R.id.axleSensorLeft);
            axleSensorCenter = itemView.findViewById(R.id.axleSensorCenter);
            axleSensorRight = itemView.findViewById(R.id.axleSensorRight);

            resetButton.setOnTouchListener(MainActivityBinder::addMotion);
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
            view.setOnClickListener(v -> clickListener.onClick(axis.getNumber(), side, isSavedState, isSelected));

            resetButton.setOnClickListener(v -> resetListener.onReset(axis.getNumber()));
        }

        private void setIcon(ImageView imageView, AxisSide side, String mac) {
            if (mac == null) {
                imageView.setImageResource(getDefaultIcon(side));
            } else if (finishedMacs.contains(mac)) {
                imageView.setImageResource(getConnfiguredIcon(side));
            } else {
                imageView.setImageResource(getSensorIcon(side));
            }
        }

        private int getDefaultIcon(AxisSide side) {
            return switch (side) {
                case LEFT -> ic_left;
                case CENTER -> ic_center;
                case RIGHT -> ic_right;
            };
        }

        private int getSensorIcon(AxisSide side) {
            return switch (side) {
                case LEFT -> ic_legt_selected;
                case CENTER -> ic_center_selected;
                case RIGHT -> ic_right_selected;
            };
        }

        private int getConnfiguredIcon(AxisSide side) {
            return switch (side) {
                case LEFT -> ic_left_saved;
                case CENTER -> ic_center_saved;
                case RIGHT -> ic_right_saved;
            };
        }
    }

    public void removeFinishedMacs(Set<String> freedMacs) {
        if (finishedMacs != null) {
            finishedMacs.removeAll(freedMacs);
            notifyDataSetChanged();
        }
    }

}