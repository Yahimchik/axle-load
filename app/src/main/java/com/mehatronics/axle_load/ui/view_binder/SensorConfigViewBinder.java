package com.mehatronics.axle_load.ui.view_binder;

import static com.mehatronics.axle_load.ui.TextWatcherFactory.createWatcher;

import com.mehatronics.axle_load.databinding.FragmentDeviceDetailsBinding;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.format.SensorConfigFormatter;

import javax.inject.Inject;

public class SensorConfigViewBinder {
    private final FragmentDeviceDetailsBinding binding;
    private final SensorConfigFormatter<SensorConfig> format;

    @Inject
    public SensorConfigViewBinder(FragmentDeviceDetailsBinding binding, SensorConfigFormatter<SensorConfig> format) {
        this.binding = binding;
        this.format = format;
    }

    public void bind(SensorConfig config) {
        binding.messageDeliveryPeriodEditText.setText(format.formatMessageDeliveryPeriod(config));
        binding.measurementPeriodEditText.setText(format.formatMeasurementPeriod(config));
        binding.stateNumber.setText(format.formatStateNumber(config));

        binding.messageDeliveryPeriodEditText
                .addTextChangedListener(
                        createWatcher(config::setMessageDeliveryPeriod)
                );
        binding.measurementPeriodEditText
                .addTextChangedListener(
                        createWatcher(config::setMeasurementPeriod)
                );
        binding.stateNumber
                .addTextChangedListener(
                        createWatcher(config::setStateNumber)
                );
    }
}
