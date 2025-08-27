package com.mehatronics.axle_load.ui.binder;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.materialswitch.MaterialSwitch;
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
        MaterialSwitch switchTrailerInput = activity.findViewById(R.id.switchTrailerInput);
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
        switchTrailerInput.setOnCheckedChangeListener((buttonView, isChecked)
                -> trailerNumber.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }
}