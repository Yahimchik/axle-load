package com.mehatronics.axle_load.ui.adapter.sensor;

import static com.mehatronics.axle_load.R.string.error_installation_point_greater_than_total_axles;
import static com.mehatronics.axle_load.R.string.error_sensor_number_greater_than_total_sensors;
import static com.mehatronics.axle_load.utils.ByteUtils.tryParseInt;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.notification.SnackbarManager;

import javax.inject.Inject;

public class SensorConfigValidator {

    private final ResourceProvider provider;
    private final SnackbarManager snackbarManager;

    @Inject
    public SensorConfigValidator(ResourceProvider provider, SnackbarManager snackbarManager) {
        this.provider = provider;
        this.snackbarManager = snackbarManager;
    }

    public boolean validateRange(EditText field, int min, int max, int errorRes) {
        String text = field.getText().toString().trim();
        try {
            int value = Integer.parseInt(text);
            if (value < min || value > max) throw new NumberFormatException();
            field.setError(null);
            return true;
        } catch (NumberFormatException e) {
            field.setError(provider.getString(errorRes));
            return false;
        }
    }

    public boolean validateLength(EditText field, int min, int max, int errorRes) {
        String text = field.getText().toString().trim();
        if (text.length() < min || text.length() > max) {
            field.setError(provider.getString(errorRes));
            return false;
        }
        field.setError(null);
        return true;
    }

    public boolean validateInstallationPoint(Spinner field, EditText text, View root) {
        String selectedAxle = field.getSelectedItem().toString();
        int totalAxles = tryParseInt(text.getText().toString());
        if (totalAxles > 0) {
            try {
                int axleNumber = Integer.parseInt(selectedAxle.split(" ")[1]);
                if (axleNumber > totalAxles) {
                    snackbarManager.showMessage(root, provider.getString(error_installation_point_greater_than_total_axles));
                    return false;
                }
            } catch (Exception e) {
                snackbarManager.showMessage(root, provider.getString(error_sensor_number_greater_than_total_sensors));
                return false;
            }
        }
        return true;
    }
}