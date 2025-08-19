package com.mehatronics.axle_load.ui.adapter.sensor;

import static com.mehatronics.axle_load.R.string.error_measurement_period_range;
import static com.mehatronics.axle_load.R.string.error_message_period_range;
import static com.mehatronics.axle_load.R.string.error_sensors_on_axle_range;
import static com.mehatronics.axle_load.R.string.error_state_number_length;
import static com.mehatronics.axle_load.R.string.error_total_axles_range;
import static com.mehatronics.axle_load.R.string.error_total_sensors_range;
import static com.mehatronics.axle_load.utils.ByteUtils.tryParseInt;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.format.SensorConfigFormatter;
import com.mehatronics.axle_load.domain.entities.SensorConfig;

public class SensorConfigAdapter {

    private final EditText messageDeliveryPeriod;
    private final EditText measurementPeriod;
    private final CheckBox trailerChangeableCheckBox;
    private final EditText totalAxlesEditText;
    private final EditText totalSensorsEditText;
    private final EditText numberOfSensorsOnAxleEditText;
    private final EditText stateNumber;
    private final Button finishButton;

    private final SensorConfigFormatter formatter;
    private final SensorConfigValidator validator;
    private final SpinnerHelper spinnerHelper;
    private final View root;
    private final boolean isSpinnerInitialized;

    public SensorConfigAdapter(View view, SensorConfigFormatter formatter,
                               SensorConfigValidator validator) {
        this.root = view;
        this.formatter = formatter;
        this.validator = validator;

        messageDeliveryPeriod = view.findViewById(R.id.messageDeliveryPeriodEditText);
        measurementPeriod = view.findViewById(R.id.measurementPeriodEditText);
        stateNumber = view.findViewById(R.id.stateNumber);
        finishButton = view.findViewById(R.id.finishButton);
        trailerChangeableCheckBox = view.findViewById(R.id.trailerChangeableCheckBox);
        totalAxlesEditText = view.findViewById(R.id.totalAxlesEditText);
        totalSensorsEditText = view.findViewById(R.id.totalSensorsEditText);
        numberOfSensorsOnAxleEditText = view.findViewById(R.id.numberOfSensorsOnAxleEditText);

        spinnerHelper = new SpinnerHelper(view.findViewById(R.id.installationPointSpinner));

        spinnerHelper.initSpinner(1, 0);
        spinnerHelper.setOnItemSelectedListener(this::validateAndToggleSaveButton);
        isSpinnerInitialized = true;

        initTextWatchers();
        trailerChangeableCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> validateAndToggleSaveButton());
    }

    public void finishButtonOnClick(View.OnClickListener listener) {
        finishButton.setOnClickListener(listener);
    }

    private void validateAndToggleSaveButton() {
        finishButton.setEnabled(isValid());
    }

    private boolean isValid() {
        boolean valid = validator.validateRange(messageDeliveryPeriod, 2, 1800, error_message_period_range);
        valid &= validator.validateRange(measurementPeriod, 2, 1800, error_measurement_period_range);
        valid &= validator.validateLength(stateNumber, 1, 10, error_state_number_length);
        valid &= validator.validateRange(totalAxlesEditText, 1, 7, error_total_axles_range);
        valid &= validator.validateRange(totalSensorsEditText, 1, 14, error_total_sensors_range);
        valid &= validator.validateRange(numberOfSensorsOnAxleEditText, 1, 2, error_sensors_on_axle_range);
        valid &= validator.validateInstallationPoint(spinnerHelper.spinner(), totalAxlesEditText, root);
        return valid;
    }

    private void initTextWatchers() {
        addWatcher(messageDeliveryPeriod);
        addWatcher(measurementPeriod);
        addWatcher(stateNumber);
        addWatcher(totalAxlesEditText);
        addWatcher(totalSensorsEditText);
        addWatcher(numberOfSensorsOnAxleEditText);
    }

    private void addWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                validateAndToggleSaveButton();
            }
        });
    }

    public void bind(SensorConfig config) {
        messageDeliveryPeriod.setText(formatter.formatMessageDeliveryPeriod(config));
        measurementPeriod.setText(formatter.formatMeasurementPeriod(config));
        stateNumber.setText(formatter.formatStateNumber(config));
        totalAxlesEditText.setText(String.valueOf(config.getTotalNumberOfAxleOnChassis()));
        totalSensorsEditText.setText(String.valueOf(config.getTotalNumberOfSensorsOnChassis()));
        numberOfSensorsOnAxleEditText.setText(String.valueOf(config.getTotalNumberOfSensorsOnAxle()));
        trailerChangeableCheckBox.setChecked(config.getCarType() == 1);

        int spinnerIndex = (config.getNumberOfAxle() - 1) * 3 + config.getInstallationPosition();
        if (isSpinnerInitialized) spinnerHelper.spinner().setSelection(spinnerIndex);
    }

    public void updateConfig(SensorConfig config) {
        config.setMessageDeliveryPeriod(tryParseInt(messageDeliveryPeriod.getText().toString()));
        config.setMeasurementPeriod(tryParseInt(measurementPeriod.getText().toString()));
        config.setStateNumber(stateNumber.getText().toString());
        config.setTotalNumberOfAxleOnChassis(tryParseInt(totalAxlesEditText.getText().toString()));
        config.setTotalNumberOfSensorsOnChassis(tryParseInt(totalSensorsEditText.getText().toString()));
        config.setNumberOfAxle(spinnerHelper.getSelectedAxle());
        config.setInstallationPosition(spinnerHelper.getSelectedPosition());
        config.setCarType(trailerChangeableCheckBox.isChecked() ? 1 : 0);
        config.setTotalNumberOfSensorsOnAxle(tryParseInt(numberOfSensorsOnAxleEditText.getText().toString()));
    }
}