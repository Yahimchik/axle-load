package com.mehatronics.axle_load.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.adapter.listener.OnAddCalibrationPointListener;
import com.mehatronics.axle_load.adapter.listener.OnDeleteCalibrationPointListener;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.utils.diffUtil.CalibrationDiffUtil;

import java.util.Locale;

public class TableAdapter extends ListAdapter<CalibrationTable, TableAdapter.ViewHolder> {

    private final OnDeleteCalibrationPointListener onDeleteListener;
    private final OnAddCalibrationPointListener onAddListener;

    public TableAdapter(OnAddCalibrationPointListener onAddListener,
                        OnDeleteCalibrationPointListener onDeleteListener) {
        super(new CalibrationDiffUtil());
        this.onAddListener = onAddListener;
        this.onDeleteListener = onDeleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calibration_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalibrationTable curr = getItem(position);

        holder.weightTextView.setText(getFormat(curr.getDetector()));
        holder.pressureTextView.setText(getFormat(curr.getMultiplier()));

        if (curr.isLast()) {
            holder.addButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);

            holder.addButton.setOnClickListener(v -> {
                var point = new CalibrationTable(curr.getDetector(), curr.getMultiplier());
                onAddListener.onAdd(point);
            });
        } else {
            holder.addButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.deleteButton.setOnClickListener(v -> onDeleteListener.onDelete(curr));
        }
    }

    private String getFormat(float value) {
        return String.format(Locale.getDefault(), "%.3f ", value);
    }

    private String getFormat(int value) {
        return String.format(Locale.getDefault(), "%d ", value);
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
