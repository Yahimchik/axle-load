package com.mehatronics.axle_load.domain.usecase;

import static com.mehatronics.axle_load.utils.constants.ValueConstants.MAX_AXES_COUNT;
import static com.mehatronics.axle_load.utils.constants.ValueConstants.MIN_AXES_COUNT;

import com.mehatronics.axle_load.ValidationResult;
import com.mehatronics.axle_load.entities.enums.ValidationError;

import javax.inject.Inject;

public class ValidateAxisCountUseCase {

    @Inject
    public ValidateAxisCountUseCase() {
    }

    public ValidationResult execute(String input) {
        if (input == null || input.isEmpty()) {
            return new ValidationResult.Error(ValidationError.EMPTY_AXIS_COUNT);
        }

        try {
            int count = Integer.parseInt(input);
            if (count < MIN_AXES_COUNT || count > MAX_AXES_COUNT) {
                return new ValidationResult.Error(ValidationError.AXIS_COUNT_OUT_OF_RANGE);
            }
            return new ValidationResult.Success(count);
        } catch (NumberFormatException e) {
            return new ValidationResult.Error(ValidationError.INVALID_NUMBER);
        }
    }
}
