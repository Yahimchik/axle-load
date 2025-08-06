package com.mehatronics.axle_load.ui.adapter.sensor;

import static android.R.layout.simple_spinner_item;
import static com.mehatronics.axle_load.constants.StringConstants.AXLE;
import static com.mehatronics.axle_load.constants.StringConstants.LEFT;
import static com.mehatronics.axle_load.constants.StringConstants.RIGHT;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.format.SensorConfigFormatter;
import com.mehatronics.axle_load.domain.entities.SensorConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SensorConfigAdapter {
    private final EditText messageDeliveryPeriod;
    private final EditText measurementPeriod;
    private final EditText stateNumber;
    private final Spinner installationPointSpinner;
    private final Button saveButton;

    private boolean isSpinnerListenerActive = false;
    private boolean isSpinnerInitialized = false;
    private Integer pendingSpinnerSelection = null;

    private final CachedValues cache = new CachedValues();
    private final SensorConfigFormatter formatter;

    private static void accept(String val) {
    }

    private static class CachedValues {
        String messageDeliveryPeriod;
        String measurementPeriod;
        String stateNumber;
        int installationPoint = -1;
    }

    public SensorConfigAdapter(View view, SensorConfigFormatter formatter) {
        messageDeliveryPeriod = view.findViewById(R.id.messageDeliveryPeriodEditText);
        measurementPeriod = view.findViewById(R.id.measurementPeriodEditText);
        stateNumber = view.findViewById(R.id.stateNumber);
        installationPointSpinner = view.findViewById(R.id.installationPointSpinner);
        saveButton = view.findViewById(R.id.saveConfigurationButton);
        this.formatter = formatter;

        initSpinner();

        addWatcher(messageDeliveryPeriod, SensorConfigAdapter::accept, () -> cache.messageDeliveryPeriod, val -> cache.messageDeliveryPeriod = val);
        addWatcher(measurementPeriod, SensorConfigAdapter::accept, () -> cache.measurementPeriod, val -> cache.measurementPeriod = val);
        addWatcher(stateNumber, SensorConfigAdapter::accept, () -> cache.stateNumber, val -> cache.stateNumber = val);
    }

    private void validateAndToggleSaveButton() {
        boolean valid = isValidInternal();
        saveButton.setEnabled(valid);
    }

    private boolean isValidRange(String value) {
        try {
            int number = Integer.parseInt(value);
            return number < 2 || number > 1800;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isValidInternal() {
        boolean valid = true;

        String messagePeriodText = messageDeliveryPeriod.getText().toString().trim();
        if (isValidRange(messagePeriodText)) {
            messageDeliveryPeriod.setError("Value must be between 2 and 1800");
            valid = false;
        } else {
            messageDeliveryPeriod.setError(null);
        }

        String measurementText = measurementPeriod.getText().toString().trim();
        if (isValidRange(measurementText)) {
            measurementPeriod.setError("Value must be between 2 and 1800");
            valid = false;
        } else {
            measurementPeriod.setError(null);
        }

        String stateNumberText = stateNumber.getText().toString().trim();
        if (stateNumberText.isEmpty() || stateNumberText.length() > 10) {
            stateNumber.setError("Max 10 characters required");
            valid = false;
        } else {
            stateNumber.setError(null);
        }

        return valid;
    }

    private void addWatcher(EditText editText, Consumer<String> onChange, Supplier<String> cacheGetter, Consumer<String> cacheSetter) {
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                String newValue = s.toString();
                if (!newValue.equals(cacheGetter.get())) {
                    cacheSetter.accept(newValue);
                    onChange.accept(newValue);
                }
                validateAndToggleSaveButton();
            }
        });
    }

    private void initSpinner() {
        List<String> pointOptions = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            pointOptions.add(getInstallationPointDescription(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                installationPointSpinner.getContext(),
                simple_spinner_item,
                pointOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        installationPointSpinner.setAdapter(adapter);

        installationPointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerListenerActive && (position + 1 != cache.installationPoint)) {
                    cache.installationPoint = position + 1;
                    validateAndToggleSaveButton();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        isSpinnerInitialized = true;
        if (pendingSpinnerSelection != null) {
            updateSpinnerSelectionIfNeeded(pendingSpinnerSelection);
            pendingSpinnerSelection = null;
        }
    }

    public void bind(SensorConfig config) {
        bindField(messageDeliveryPeriod, formatter.formatMessageDeliveryPeriod(config), val -> cache.messageDeliveryPeriod = val);
        bindField(measurementPeriod, formatter.formatMeasurementPeriod(config), val -> cache.measurementPeriod = val);
        bindField(stateNumber, formatter.formatStateNumber(config), val -> cache.stateNumber = val);
        updateSpinnerSelectionIfNeeded(config.getInstallationPoint());
    }

    private void bindField(EditText field, String newValue, Consumer<String> cacheSetter) {
        if (!newValue.equals(field.getText().toString())) {
            field.setText(newValue);
            cacheSetter.accept(newValue);
        }
    }

    private void updateSpinnerSelectionIfNeeded(int newPoint) {
        if (!isSpinnerInitialized) {
            pendingSpinnerSelection = newPoint;
            return;
        }

        if (newPoint != cache.installationPoint) {
            isSpinnerListenerActive = false;
            installationPointSpinner.setSelection(newPoint - 1);
            cache.installationPoint = newPoint;
        }
        isSpinnerListenerActive = true;
    }

    public void updateConfig(SensorConfig config) {
        config.setMessageDeliveryPeriod(Integer.parseInt(cache.messageDeliveryPeriod));
        config.setMeasurementPeriod(Integer.parseInt(cache.measurementPeriod));
        config.setStateNumber(cache.stateNumber);
        config.setInstallationPoint(cache.installationPoint);
    }

    private String getInstallationPointDescription(int installationPoint) {
        int axle = (installationPoint - 1) / 2 + 1;
        String position = (installationPoint - 1) % 2 == 0 ? LEFT : RIGHT;
        return AXLE + " " + axle + " — " + position;
    }
}
