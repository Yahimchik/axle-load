package com.mehatronics.axle_load.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        if (curr.isLast()) {
            holder.addButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.bright_sky_blue));
            holder.addButton.setOnClickListener(v -> {
                var point = new CalibrationTable(curr.getDetector(), curr.getMultiplier());
                onAddListener.onAdd(point);
            });
        } else {
            holder.addButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.card_background_color));
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
        }
    }

    private String getFormat(float value) {
        return String.format(Locale.getDefault(), "%.1f ", value);
    }

    private String getFormat(int value) {
        return String.format(Locale.getDefault(), "%d ", value);
    }

}

//package com.mehatronics.axle_load.ui.adapter;
//
//import android.text.InputFilter;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.ListAdapter;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.mehatronics.axle_load.R;
//import com.mehatronics.axle_load.domain.entities.CalibrationTable;
//import com.mehatronics.axle_load.ui.adapter.diffUtil.CalibrationDiffUtil;
//import com.mehatronics.axle_load.ui.adapter.listener.OnAddCalibrationPointListener;
//import com.mehatronics.axle_load.ui.adapter.listener.OnDeleteCalibrationPointListener;
//import com.mehatronics.axle_load.ui.adapter.sensor.DecimalDigitsInputFilter;
//import com.mehatronics.axle_load.ui.adapter.sensor.SimpleTextWatcher;
//
//import java.util.Locale;
//
//public class TableAdapter extends ListAdapter<CalibrationTable, TableAdapter.ViewHolder> {
//    private final OnDeleteCalibrationPointListener onDeleteListener;
//    private final OnAddCalibrationPointListener onAddListener;
//
//    public TableAdapter(OnAddCalibrationPointListener onAddListener,
//                        OnDeleteCalibrationPointListener onDeleteListener) {
//        super(new CalibrationDiffUtil());
//        this.onAddListener = onAddListener;
//        this.onDeleteListener = onDeleteListener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.calibration_table, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        CalibrationTable curr = getItem(position);
//
//        holder.weightEditText.setText(getFormat(curr.getDetector()));
//        holder.pressureEditText.setText(getFormat(curr.getMultiplier()));
//
//        // Очистка старых вотчеров
//        if (holder.weightEditText.getTag() instanceof TextWatcher) {
//            holder.weightEditText.removeTextChangedListener((TextWatcher) holder.weightEditText.getTag());
//        }
//
//        if (holder.pressureEditText.getTag() instanceof TextWatcher) {
//            holder.pressureEditText.removeTextChangedListener((TextWatcher) holder.pressureEditText.getTag());
//        }
//
//        TextWatcher weightWatcher = new SimpleTextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    int detector = Integer.parseInt(s.toString());
//                    if (detector < 1 || detector > 99999) {
//                        holder.weightEditText.setError("1–99999");
//                    } else {
//                        curr.setDetector(detector);
//                        holder.weightEditText.setError(null);
//                    }
//                } catch (NumberFormatException e) {
//                    holder.weightEditText.setError("Введите число");
//                }
//            }
//        };
//        holder.weightEditText.addTextChangedListener(weightWatcher);
//        holder.weightEditText.setTag(weightWatcher);
//        holder.weightEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
//
//        TextWatcher pressureWatcher = new SimpleTextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    float multiplier = Float.parseFloat(s.toString());
//                    if (multiplier < 0.001f || multiplier > 99.999f) {
//                        holder.pressureEditText.setError("0.001–99.999");
//                    } else {
//                        curr.setMultiplier(multiplier);
//                        holder.pressureEditText.setError(null);
//                    }
//                } catch (NumberFormatException e) {
//                    holder.pressureEditText.setError("Введите число");
//                }
//            }
//        };
//        holder.pressureEditText.addTextChangedListener(pressureWatcher);
//        holder.pressureEditText.setTag(pressureWatcher);
//        holder.pressureEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
//
//        if (curr.isLast()) {
//            holder.addButton.setVisibility(View.VISIBLE);
//            holder.deleteButton.setVisibility(View.INVISIBLE);
//
//            holder.addButton.setOnClickListener(v -> {
//                var point = new CalibrationTable(curr.getDetector(), curr.getMultiplier());
//                onAddListener.onAdd(point);
//            });
//        } else {
//            holder.addButton.setVisibility(View.INVISIBLE);
//            holder.deleteButton.setVisibility(View.VISIBLE);
//
//            holder.deleteButton.setOnClickListener(v -> onDeleteListener.onDelete(curr));
//        }
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        EditText weightEditText;
//        EditText pressureEditText;
//        ImageButton addButton;
//        ImageButton deleteButton;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            weightEditText = itemView.findViewById(R.id.weightValue);
//            pressureEditText = itemView.findViewById(R.id.pressureValue);
//            addButton = itemView.findViewById(R.id.addButton);
//            deleteButton = itemView.findViewById(R.id.deleteButton);
//        }
//    }
//
//    private String getFormat(float value) {
//        return String.format(Locale.getDefault(), "%.3f", value);
//    }
//
//    private String getFormat(int value) {
//        return String.format(Locale.getDefault(), "%d", value);
//    }
//}
