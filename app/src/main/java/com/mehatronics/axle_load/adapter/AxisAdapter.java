package com.mehatronics.axle_load.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;

public class AxisAdapter extends RecyclerView.Adapter<AxisAdapter.ViewHolder> {

    private int numberOfAxes;
    private OnAxisClickListener onAxisClickListener;  // Интерфейс для обработки кликов

    // Конструктор
    public AxisAdapter(int numberOfAxes, OnAxisClickListener onAxisClickListener) {
        this.numberOfAxes = numberOfAxes;
        this.onAxisClickListener = onAxisClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNumberOfAxes(int numberOfAxes) {
        this.numberOfAxes = numberOfAxes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_axis, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.axisTitle.setText("Ось " + (position + 1));
        holder.axleLeft.setImageResource(R.drawable.axle_sensor_left);
        holder.axleCenter.setImageResource(R.drawable.axle_sensor_center);
        holder.axisRight.setImageResource(R.drawable.axes_sensor_right);

        holder.axleLeft.setOnClickListener(v -> onAxisClickListener.onAxisClick("left", position));
        holder.axleCenter.setOnClickListener(v -> onAxisClickListener.onAxisClick("center", position));
        holder.axisRight.setOnClickListener(v -> onAxisClickListener.onAxisClick("right", position));
    }

    @Override
    public int getItemCount() {
        return numberOfAxes;
    }
    public interface OnAxisClickListener {
        void onAxisClick(String sensorPosition, int axisPosition);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView axisTitle;
        ImageView axleLeft;
        ImageView axleCenter;
        ImageView axisRight;

        public ViewHolder(View itemView) {
            super(itemView);
            axisTitle = itemView.findViewById(R.id.axisTitle);
            axleLeft = itemView.findViewById(R.id.axleSensorLeft);
            axleCenter = itemView.findViewById(R.id.axleSensorCenter);
            axisRight = itemView.findViewById(R.id.axleSensorRight);
        }
    }
}
