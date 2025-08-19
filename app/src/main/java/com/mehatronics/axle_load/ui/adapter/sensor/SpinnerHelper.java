package com.mehatronics.axle_load.ui.adapter.sensor;

import static android.R.layout.simple_spinner_item;
import static com.mehatronics.axle_load.constants.StringConstants.*;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public record SpinnerHelper(Spinner spinner) {
    public void initSpinner(int selectedAxle, int selectedPosition) {
        spinner.setAdapter(createAdapter(spinner.getContext()));
        int index = (selectedAxle - 1) * 3 + selectedPosition;
        spinner.setSelection(index);
    }

    @NonNull
    private ArrayAdapter<String> createAdapter(Context context) {
        List<String> points = new ArrayList<>();
        for (int axle = 1; axle <= 7; axle++) {
            for (int pos = 0; pos <= 2; pos++) {
                points.add(getDescription(axle, pos));
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, simple_spinner_item, points);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private String getDescription(int axle, int position) {
        String pos = switch (position) {
            case 0 -> CENTER;
            case 1 -> LEFT;
            case 2 -> RIGHT;
            default -> UNKNOWN;
        };
        return AXLE + " " + axle + " — " + pos;
    }

    public int getSelectedAxle() {
        return (spinner.getSelectedItemPosition() / 3) + 1;
    }

    public int getSelectedPosition() {
        return spinner.getSelectedItemPosition() % 3;
    }

    public void setOnItemSelectedListener(Runnable onSelected) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSelected.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}