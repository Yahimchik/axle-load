package com.mehatronics.axle_load.domain.usecase.impl;

import static com.mehatronics.axle_load.constants.ValueConstants.MAX_AXES_COUNT;
import static com.mehatronics.axle_load.constants.ValueConstants.MIN_AXES_COUNT;

import com.mehatronics.axle_load.domain.entities.ValidationResult;
import com.mehatronics.axle_load.domain.usecase.ValidateAxisCountUseCase;
import com.mehatronics.axle_load.domain.entities.enums.ValidationError;

import javax.inject.Inject;

public class ValidateAxisCountUseCaseImpl implements ValidateAxisCountUseCase {

    @Inject
    public ValidateAxisCountUseCaseImpl() {
    }

    public ValidationResult execute(String input) {
        if (input == null || input.isEmpty()) {
            return new ValidationResult.Error(ValidationError.EMPTY_AXIS);
        }

        try {
            int count = Integer.parseInt(input);
            if (count < MIN_AXES_COUNT || count > MAX_AXES_COUNT) {
                return new ValidationResult.Error(ValidationError.AXIS_OUT_OF_RANGE);
            }
            return new ValidationResult.Success(count);
        } catch (NumberFormatException e) {
            return new ValidationResult.Error(ValidationError.INVALID_NUMBER);
        }
    }
}
