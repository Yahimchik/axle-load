package com.mehatronics.axle_load.domain.usecase;

import com.mehatronics.axle_load.domain.entities.ValidationResult;

public interface ValidateAxisCountUseCase {
    ValidationResult execute(String input);
}