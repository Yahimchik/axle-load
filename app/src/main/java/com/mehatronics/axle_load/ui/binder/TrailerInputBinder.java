package com.mehatronics.axle_load.ui.binder;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.repository.DeviceRepository;

import javax.inject.Inject;

public class TrailerInputBinder {

    private final DeviceRepository deviceRepository;

    @Inject
    public TrailerInputBinder(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public void bind(Activity activity) {
        MaterialButton btnChangeTrailer = activity.findViewById(R.id.buttonChangeTrailer);
        TextInputLayout trailerNumber = activity.findViewById(R.id.trailerNumberLayout);
        TextInputEditText editText = activity.findViewById(R.id.trailerNumberEditText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                deviceRepository.setStateNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        changeTrailerOnClick(btnChangeTrailer, trailerNumber, editText);
    }

    private static void changeTrailerOnClick(MaterialButton btnChangeTrailer, TextInputLayout trailerNumber, TextInputEditText editText) {
        btnChangeTrailer.setOnClickListener(v -> {
            trailerNumber.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);

            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

            btnChangeTrailer.setEnabled(false);
        });
    }
}