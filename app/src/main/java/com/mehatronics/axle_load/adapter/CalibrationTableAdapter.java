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
        this.calibrationPoints = new ArrayList<>(calibrationPoints);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<CalibrationTable> newCalibrationPoints) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new CalibrationDiffCallback(this.calibrationPoints, newCalibrationPoints)
        );

        this.calibrationPoints.clear();
        this.calibrationPoints.addAll(newCalibrationPoints);
        Log.d("MyTag", String.valueOf(newCalibrationPoints));

        diffResult.dispatchUpdatesTo(this);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calibration_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return calibrationPoints.size() - 2;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int actualIndex = position + 1; // пропускаем первый элемент
        CalibrationTable table = calibrationPoints.get(actualIndex);

        holder.weightTextView.setText(String.format("%d ", table.getDetector()));
        holder.pressureTextView.setText(String.format("%.3f ", table.getMultiplier()));

        // Показывать кнопку "удалить" для всех кроме последнего отображаемого
        if (actualIndex < calibrationPoints.size() - 2) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                calibrationPoints.remove(actualIndex);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            });
        } else {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }

        // Показывать кнопку "добавить" только на последнем отображаемом элементе
        if (actualIndex == calibrationPoints.size() - 2) {
            holder.addButton.setVisibility(View.VISIBLE);
            holder.addButton.setOnClickListener(v -> {
                CalibrationTable newCalibration = new CalibrationTable(
                        table.getDetector(),
                        table.getMultiplier());

                calibrationPoints.add(calibrationPoints.size() - 1, newCalibration); // вставка перед последним
                notifyItemInserted(position + 1);
                notifyItemChanged(position); // чтобы скрыть кнопку add у предыдущего
                updateVirtualPoint(newCalibration);
            });
        } else {
            holder.addButton.setVisibility(View.INVISIBLE);
        }
    }

    public void updateVirtualPoint(CalibrationTable virtualPoint) {
        if (calibrationPoints.isEmpty()) return;

        int lastPos = calibrationPoints.size() - 2;
        CalibrationTable oldItem = calibrationPoints.get(lastPos);

        if (oldItem.getDetector() != virtualPoint.getDetector()
                || oldItem.getMultiplier() != virtualPoint.getMultiplier()) {
            calibrationPoints.set(lastPos, virtualPoint);
            notifyItemChanged(lastPos);
        }
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