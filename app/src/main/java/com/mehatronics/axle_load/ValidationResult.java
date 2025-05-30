package com.mehatronics.axle_load;

import com.mehatronics.axle_load.entities.enums.ValidationError;

public sealed class ValidationResult {
    public static final class Success extends ValidationResult {
        private final int count;

        public Success(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static final class Error extends ValidationResult {
        private final ValidationError error;

        public Error(ValidationError error) {
            this.error = error;
        }

        public ValidationError getError() {
            return error;
        }
    }
}

