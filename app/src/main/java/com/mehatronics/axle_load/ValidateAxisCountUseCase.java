package com.mehatronics.axle_load;

import static com.mehatronics.axle_load.utils.constants.ValueConstants.MAX_AXES_COUNT;
import static com.mehatronics.axle_load.utils.constants.ValueConstants.MIN_AXES_COUNT;

import javax.inject.Inject;

public class ValidateAxisCountUseCase {

    @Inject
    public ValidateAxisCountUseCase() {
    }

    public ValidationResult execute(String input) {
        if (input == null || input.isEmpty()) {
            return new ValidationResult.Error("Введите количество осей");
        }

        try {
            int count = Integer.parseInt(input);
            if (count < MIN_AXES_COUNT || MAX_AXES_COUNT > 8) {
                return new ValidationResult.Error("Введите число от 1 до 8");
            }
            return new ValidationResult.Success(count);
        } catch (NumberFormatException e) {
            return new ValidationResult.Error("Введите корректное число");
        }
    }
}
