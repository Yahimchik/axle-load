package com.mehatronics.axle_load.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.CalibrationTable;

import java.util.List;

public class CalibrationTableAdapter extends RecyclerView.Adapter<CalibrationTableAdapter.ViewHolder> {
    private final List<CalibrationTable> calibrationPoints;

    public CalibrationTableAdapter(List<CalibrationTable> calibrationPoints) {
        if (calibrationPoints.size() > 2) {
            this.calibrationPoints = calibrationPoints.subList(1, calibrationPoints.size() - 1);
        } else {
            this.calibrationPoints = calibrationPoints;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<CalibrationTable> newCalibrationPoints) {
        if (newCalibrationPoints.size() > 2) {
            calibrationPoints.clear();
            calibrationPoints.addAll(newCalibrationPoints.subList(1, newCalibrationPoints.size() - 1));
        } else {
            calibrationPoints.clear();
            calibrationPoints.addAll(newCalibrationPoints);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calibration_table, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalibrationTable table = calibrationPoints.get(position);
        holder.weightTextView.setText(String.format("%d ", table.getDetector()));
        holder.pressureTextView.setText(String.format("%.3f ",
                (calibrationPoints.getLast().getDetector()
                        - calibrationPoints.getFirst().getDetector())
                        * table.getMultiplier()));
    }

    @Override
    public int getItemCount() {
        return calibrationPoints.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView weightTextView;
        TextView pressureTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weightValue);
            pressureTextView = itemView.findViewById(R.id.pressureValue);
        }
    }
}