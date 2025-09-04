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

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.ui.adapter.diffUtil.CalibrationDiffUtil;
import com.mehatronics.axle_load.ui.adapter.listener.OnAddCalibrationPointListener;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeleteCalibrationPointListener;
import com.mehatronics.axle_load.ui.adapter.sensor.SimpleTextWatcher;

import java.util.List;
import java.util.Locale;

public class TableAdapter extends ListAdapter<CalibrationTable, TableAdapter.ViewHolder> {

    private final OnDeleteCalibrationPointListener onDeleteListener;
    private final OnAddCalibrationPointListener onAddListener;

    public TableAdapter(OnAddCalibrationPointListener onAddListener,
                        OnDeleteCalibrationPointListener onDeleteListener) {
        super(new CalibrationDiffUtil());
        this.onAddListener = onAddListener;
        this.onDeleteListener = onDeleteListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calibration_table, parent, false);
        return new ViewHolder(view, onAddListener, onDeleteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final EditText weightEditText;
        private final EditText pressureEditText;
        private final ImageButton addButton;
        private final ImageButton deleteButton;
        private CalibrationTable boundItem;

        public ViewHolder(@NonNull View itemView,
                          OnAddCalibrationPointListener onAddListener,
                          OnDeleteCalibrationPointListener onDeleteListener) {
            super(itemView);

            weightEditText = itemView.findViewById(R.id.weightValue);
            pressureEditText = itemView.findViewById(R.id.pressureValue);
            addButton = itemView.findViewById(R.id.addButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            weightEditText.setSaveEnabled(false);
            pressureEditText.setSaveEnabled(false);

            weightEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (boundItem != null) {
                        try {
                            boundItem.setDetector(Integer.parseInt(s.toString()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            });

            pressureEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (boundItem != null) {
                        try {
                            boundItem.setMultiplier(Float.parseFloat(s.toString()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            });

            weightEditText.setOnEditorActionListener((v, actionId, event) -> {
                v.clearFocus();
                hideKeyboard(v);
                return false;
            });

            pressureEditText.setOnEditorActionListener((v, actionId, event) -> {
                v.clearFocus();
                hideKeyboard(v);
                return false;
            });

            addButton.setOnClickListener(v -> {
                if (boundItem != null) {
                    CalibrationTable point = new CalibrationTable(boundItem.getDetector(), boundItem.getMultiplier());
                    onAddListener.onAdd(point);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (boundItem != null) {
                    onDeleteListener.onDelete(boundItem);
                }
            });
        }

        void bind(CalibrationTable item) {
            this.boundItem = item;

            updateEditText(weightEditText, formatInt(item.getDetector()));
            updateEditText(pressureEditText, formatFloat(item.getMultiplier()));

            updateButtons(item);
        }

        void updateButtons(CalibrationTable item) {
            boolean showAdd = item.isLast();
            if (addButton.getVisibility() != (showAdd ? View.VISIBLE : View.GONE)) {
                addButton.setVisibility(showAdd ? View.VISIBLE : View.GONE);
                deleteButton.setVisibility(showAdd ? View.GONE : View.VISIBLE);
            }
        }

        private void updateEditText(EditText editText, String newValue) {
            if (!editText.getText().toString().equals(newValue)) {
                int cursor = editText.getSelectionStart();
                editText.setText(newValue);
                editText.setSelection(Math.min(cursor, newValue.length()));
            }
        }

        private static String formatFloat(float value) {
            return String.format(Locale.getDefault(), "%.1f", value);
        }

        private static String formatInt(int value) {
            return String.format(Locale.getDefault(), "%d", value);
        }

        private static void hideKeyboard(View view) {
            InputMethodManager imm = (InputMethodManager)
                    view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}