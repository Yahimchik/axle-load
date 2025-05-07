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

import java.util.ArrayList;
import java.util.List;

public class CalibrationTableAdapter extends RecyclerView.Adapter<CalibrationTableAdapter.ViewHolder> {
    private final List<CalibrationTable> calibrationPoints;

    public CalibrationTableAdapter(List<CalibrationTable> calibrationPoints) {
        if (calibrationPoints.size() > 2) {
            this.calibrationPoints = new ArrayList<>(calibrationPoints.subList(1, calibrationPoints.size() - 1));
        } else {
            this.calibrationPoints = new ArrayList<>(calibrationPoints);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<CalibrationTable> newCalibrationPoints) {
        List<CalibrationTable> filteredList;
        if (newCalibrationPoints.size() > 2) {
            filteredList = new ArrayList<>(newCalibrationPoints.subList(1, newCalibrationPoints.size() - 1));
        } else {
            filteredList = new ArrayList<>(newCalibrationPoints);
        }

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new CalibrationDiffCallback(this.calibrationPoints, filteredList)
        );

        this.calibrationPoints.clear();
        this.calibrationPoints.addAll(filteredList);

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
        CalibrationTable table = calibrationPoints.get(position);
        holder.weightTextView.setText(String.format("%d ", table.getDetector()));
        holder.pressureTextView.setText(String.format("%.3f ", table.getMultiplier()));

        if (position != calibrationPoints.size() - 1) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                calibrationPoints.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, calibrationPoints.size());
            });
        } else {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }

        if (position == calibrationPoints.size() - 1) {
            holder.addButton.setVisibility(View.VISIBLE);
            holder.addButton.setOnClickListener(v -> {
                CalibrationTable newCalibration = new CalibrationTable(table.getDetector(), 0);

                Log.d("MyTag", newCalibration.toString());
                calibrationPoints.add(newCalibration);

                int newLastIndex = calibrationPoints.size() - 1;
                notifyItemInserted(newLastIndex);
                notifyItemChanged(newLastIndex - 1);

                updateVirtualPoint(newCalibration);
            });
        } else {
            holder.addButton.setVisibility(View.INVISIBLE);
        }
    }


    public void updateVirtualPoint(CalibrationTable virtualPoint) {
        if (calibrationPoints.isEmpty()) return;

        int lastPos = calibrationPoints.size() - 1;
        CalibrationTable oldItem = calibrationPoints.get(lastPos);

        if (oldItem.getDetector() != virtualPoint.getDetector()
                || oldItem.getMultiplier() != virtualPoint.getMultiplier()) {
            calibrationPoints.set(lastPos, virtualPoint);
            notifyItemChanged(lastPos);
        }
    }

    @Override
    public int getItemCount() {
        return calibrationPoints.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView weightTextView;
        TextView pressureTextView;
        ImageButton deleteButton;
        ImageButton addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weightTextView = itemView.findViewById(R.id.weightValue);
            pressureTextView = itemView.findViewById(R.id.pressureValue);
            deleteButton = itemView.findViewById(R.id.actionButton);
            addButton = itemView.findViewById(R.id.actionAddButton);
        }
    }

    private static class CalibrationDiffCallback extends DiffUtil.Callback {
        private final List<CalibrationTable> oldList;
        private final List<CalibrationTable> newList;

        public CalibrationDiffCallback(List<CalibrationTable> oldList, List<CalibrationTable> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            CalibrationTable oldItem = oldList.get(oldItemPosition);
            CalibrationTable newItem = newList.get(newItemPosition);
            return oldItem.getDetector() == newItem.getDetector()
                    && oldItem.getMultiplier() == newItem.getMultiplier();
        }
    }
}