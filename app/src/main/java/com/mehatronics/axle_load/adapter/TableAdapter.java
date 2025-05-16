package com.mehatronics.axle_load.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.utils.diffUtil.CalibrationDiffUtil;

import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    private final List<CalibrationTable> calibrationPoints = new ArrayList<>();

    public void updateData(List<CalibrationTable> newCalibrationPoints) {
        CalibrationDiffUtil diffCallback = new CalibrationDiffUtil(this.calibrationPoints, newCalibrationPoints);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.calibrationPoints.clear();
        this.calibrationPoints.addAll(newCalibrationPoints);

        diffResult.dispatchUpdatesTo(this);
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
        var table = calibrationPoints.get(position + 1);
        holder.weightTextView.setText(String.format("%d ", table.getDetector()));
        holder.pressureTextView.setText(String.format("%.3f ", table.getMultiplier()));
        holder.addButton.setOnClickListener(v -> Log.d("MyTag", "Hello"));
    }

    @Override
    public int getItemCount() {
        return calibrationPoints.size() - 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView weightTextView;
        TextView pressureTextView;
        ImageButton addButton;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weightValue);
            pressureTextView = itemView.findViewById(R.id.pressureValue);
            addButton = itemView.findViewById(R.id.addButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}