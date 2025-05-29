package com.mehatronics.axle_load;

public abstract class ValidationResult {
    public static class Success extends ValidationResult {
        private final int count;

        public Success(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static class Error extends ValidationResult {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
