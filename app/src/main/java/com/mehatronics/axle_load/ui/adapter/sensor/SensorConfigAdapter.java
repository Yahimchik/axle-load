package com.mehatronics.axle_load.ui.adapter.sensor;

import static com.mehatronics.axle_load.R.string.error_measurement_period_range;
import static com.mehatronics.axle_load.R.string.error_message_period_range;
import static com.mehatronics.axle_load.R.string.error_sensors_on_axle_range;
import static com.mehatronics.axle_load.R.string.error_state_number_length;
import static com.mehatronics.axle_load.R.string.error_total_axles_range;
import static com.mehatronics.axle_load.R.string.error_total_sensors_greater_than_axles_times_two;
import static com.mehatronics.axle_load.R.string.error_total_sensors_range;
import static com.mehatronics.axle_load.utils.ByteUtils.tryParseInt;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.format.SensorConfigFormatter;
import com.mehatronics.axle_load.domain.entities.SensorConfig;

public class SensorConfigAdapter {
    private final EditText numberOfSensorsOnAxleEditText;
    private final EditText messageDeliveryPeriod;
    private final EditText totalSensorsEditText;
    private final EditText totalAxlesEditText;
    private final EditText measurementPeriod;
    private final EditText stateNumber;
    private final Button finishButton;

    private final ImageButton totalAxlesMinus;
    private final ImageButton totalAxlesPlus;
    private final ImageButton totalSensorsMinus;
    private final ImageButton totalSensorsPlus;
    private final ImageButton sensorsPerAxleMinus;
    private final ImageButton sensorsPerAxlePlus;
    private final ImageButton messageDeliveryMinus;
    private final ImageButton messageDeliveryPlus;
    private final ImageButton measurementPeriodMinus;
    private final ImageButton measurementPeriodPlus;
    private final TextView choiceYes;
    private final TextView choiceNo;
    private int carType = 0;
    private final SensorConfigFormatter formatter;
    private final SensorConfigValidator validator;
    private final SpinnerHelper spinnerHelper;
    private final View root;
    private final boolean isSpinnerInitialized;

    private final int selectedBgColor;
    private final int unselectedBgColor;
    private final int selectedTextColor;
    private final int unselectedTextColor;
    public SensorConfigAdapter(View view, SensorConfigFormatter formatter,
                               SensorConfigValidator validator) {
        this.root = view;
        this.formatter = formatter;
        this.validator = validator;

        messageDeliveryPeriod = view.findViewById(R.id.messageDeliveryPeriodEditText);
        measurementPeriod = view.findViewById(R.id.measurementPeriodEditText);
        stateNumber = view.findViewById(R.id.stateNumber);
        finishButton = view.findViewById(R.id.finishButton);
        totalAxlesEditText = view.findViewById(R.id.totalAxlesEditText);
        totalSensorsEditText = view.findViewById(R.id.totalSensorsEditText);
        numberOfSensorsOnAxleEditText = view.findViewById(R.id.numberOfSensorsOnAxleEditText);

        totalAxlesMinus = view.findViewById(R.id.totalAxlesMinus);
        totalAxlesPlus = view.findViewById(R.id.totalAxlesPlus);
        totalSensorsMinus = view.findViewById(R.id.totalSensorsMinus);
        totalSensorsPlus = view.findViewById(R.id.totalSensorsPlus);
        sensorsPerAxleMinus = view.findViewById(R.id.sensorsPerAxleMinus);
        sensorsPerAxlePlus = view.findViewById(R.id.sensorsPerAxlePlus);

        messageDeliveryMinus = view.findViewById(R.id.messageDeliveryMinus);
        messageDeliveryPlus = view.findViewById(R.id.messageDeliveryPlus);
        measurementPeriodMinus = view.findViewById(R.id.measurementPeriodMinus);
        measurementPeriodPlus = view.findViewById(R.id.measurementPeriodPlus);

        choiceYes = view.findViewById(R.id.choiceYes);
        choiceNo = view.findViewById(R.id.choiceNo);

        spinnerHelper = new SpinnerHelper(view.findViewById(R.id.installationPointSpinner));

        spinnerHelper.initSpinner(1, 0);
        spinnerHelper.setOnItemSelectedListener(this::validateAndToggleSaveButton);
        isSpinnerInitialized = true;

        selectedBgColor = Color.parseColor("#0094FF");
        unselectedBgColor = Color.WHITE;
        selectedTextColor = Color.WHITE;
        unselectedTextColor = Color.BLACK;

        setupIncrementDecrement();
        setupChoiceListeners();
        initTextWatchers();
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
        valid &= validator.validateSensorCount(totalSensorsEditText, totalAxlesEditText, error_total_sensors_greater_than_axles_times_two);
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

        if (config.getCarType() == 1) selectYes(false);
        else selectNo(false);

        int spinnerIndex = (config.getNumberOfAxle() - 1) * 3 + config.getInstallationPosition();
        if (isSpinnerInitialized) spinnerHelper.spinner().setSelection(spinnerIndex);
    }

    public void updateConfig(SensorConfig config) {
        config.setMessageDeliveryPeriod(tryParseInt(messageDeliveryPeriod.getText().toString()));
        config.setMeasurementPeriod(tryParseInt(measurementPeriod.getText().toString()));
        config.setStateNumber(stateNumber.getText().toString().trim());
        config.setTotalNumberOfAxleOnChassis(tryParseInt(totalAxlesEditText.getText().toString()));
        config.setTotalNumberOfSensorsOnChassis(tryParseInt(totalSensorsEditText.getText().toString()));
        config.setNumberOfAxle(spinnerHelper.getSelectedAxle());
        config.setInstallationPosition(spinnerHelper.getSelectedPosition());
        config.setCarType(carType);
        config.setTotalNumberOfSensorsOnAxle(tryParseInt(numberOfSensorsOnAxleEditText.getText().toString()));
    }

    private void setupIncrementDecrement() {
        setupButton(totalAxlesMinus, totalAxlesPlus, totalAxlesEditText, 1, 7);
        setupButton(totalSensorsMinus, totalSensorsPlus, totalSensorsEditText, 1, 14);
        setupButton(sensorsPerAxleMinus, sensorsPerAxlePlus, numberOfSensorsOnAxleEditText, 1, 2);
        setupButton(messageDeliveryMinus, messageDeliveryPlus, messageDeliveryPeriod, 2, 1800);
        setupButton(measurementPeriodMinus, measurementPeriodPlus, measurementPeriod, 2, 1800);
    }

    private void setupButton(ImageButton minus, ImageButton plus, EditText editText, int min, int max) {
        minus.setOnClickListener(v -> {
            int val = Integer.parseInt(editText.getText().toString());
            if (val > min) editText.setText(String.valueOf(val - 1));
        });
        plus.setOnClickListener(v -> {
            int val = Integer.parseInt(editText.getText().toString());
            if (val < max) editText.setText(String.valueOf(val + 1));
        });

        editText.setFilters(new InputFilter[]{new InputFilterMinMax(min, max)});
    }

    private void setupChoiceListeners() {
        choiceYes.setOnClickListener(v -> {
            selectYes(true);
            validateAndToggleSaveButton();
        });

        choiceNo.setOnClickListener(v -> {
            selectNo(true);
            validateAndToggleSaveButton();
        });
    }

    private void selectYes(boolean animate) {
        animateColors(choiceYes, unselectedBgColor, selectedBgColor,
                unselectedTextColor, selectedTextColor, animate, true);
        animateColors(choiceNo, selectedBgColor, unselectedBgColor,
                selectedTextColor, unselectedTextColor, animate, false);

        carType = 1;
    }

    private void selectNo(boolean animate) {
        animateColors(choiceNo, unselectedBgColor, selectedBgColor,
                unselectedTextColor, selectedTextColor, animate, true);
        animateColors(choiceYes, selectedBgColor, unselectedBgColor,
                selectedTextColor, unselectedTextColor, animate, false);

        carType = 0;
    }


    private void animateColors(TextView view, int fromBg, int toBg, int fromText, int toText, boolean animate, boolean selected) {
        if (!animate) {
            setViewBackground(view, toBg, selected);
            view.setTextColor(toText);
            return;
        }

        ValueAnimator bgAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromBg, toBg);
        bgAnimator.setDuration(400);
        bgAnimator.addUpdateListener(anim -> setViewBackground(view, (int) anim.getAnimatedValue(), selected));
        bgAnimator.start();

        ValueAnimator textAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromText, toText);
        textAnimator.setDuration(400);
        textAnimator.addUpdateListener(anim -> view.setTextColor((int) anim.getAnimatedValue()));
        textAnimator.start();
    }

    private void setViewBackground(TextView view, int color, boolean selected) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(selected ? 12f : 0f);
        view.setBackground(drawable);
    }

    public static class InputFilterMinMax implements InputFilter {
        private final int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                String newVal = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend);
                int input = Integer.parseInt(newVal);
                if (input < min || input > max) return "";
            } catch (NumberFormatException e) {
                return "";
            }
            return null;
        }
    }
}