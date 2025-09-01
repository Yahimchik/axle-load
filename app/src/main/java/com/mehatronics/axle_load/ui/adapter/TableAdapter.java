package com.mehatronics.axle_load.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.ui.adapter.listener.OnAddCalibrationPointListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeleteCalibrationPointListener;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.ui.adapter.diffUtil.CalibrationDiffUtil;
import com.mehatronics.axle_load.ui.adapter.sensor.SimpleTextWatcher;

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

        holder.weightEditText.setText(getFormat(curr.getDetector()));
        holder.pressureEditText.setText(getFormat(curr.getMultiplier()));

        holder.weightEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int detector = Integer.parseInt(s.toString());
                    curr.setDetector(detector);
                } catch (NumberFormatException ignored) {}
            }
        });

        holder.pressureEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    float multiplier = Float.parseFloat(s.toString());
                    curr.setMultiplier(multiplier);
                } catch (NumberFormatException ignored) {}
            }
        });

        holder.weightEditText.setOnEditorActionListener((v, actionId, event) -> {
            v.clearFocus();
            hideKeyboard(v);
            return false;
        });

        holder.pressureEditText.setOnEditorActionListener((v, actionId, event) -> {
            v.clearFocus();
            hideKeyboard(v);
            return false;
        });

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText weightEditText;
        EditText pressureEditText;
        ImageButton addButton;
        ImageButton deleteButton;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weightEditText = itemView.findViewById(R.id.weightValue);
            pressureEditText = itemView.findViewById(R.id.pressureValue);
            addButton = itemView.findViewById(R.id.addButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            cardView = itemView.findViewById(R.id.cardView);

            weightEditText.setSaveEnabled(false);
            pressureEditText.setSaveEnabled(false);
        }
    }

    private String getFormat(float value) {
        return String.format(Locale.getDefault(), "%.1f ", value);
    }

    private String getFormat(int value) {
        return String.format(Locale.getDefault(), "%d ", value);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)
                view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}