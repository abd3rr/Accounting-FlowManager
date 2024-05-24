package fr.uha.AccountingFlowManager.exception;


public class InvoiceExceptions {

    public static class InvalidReductionException extends RuntimeException {
        public InvalidReductionException(String message) {
            super(message);
        }
    }

    public static class InvalidAdditionalReductionException extends RuntimeException {
        public InvalidAdditionalReductionException(String message) {
            super(message);
        }
    }

    public static class InvalidShippingCostTypeException extends RuntimeException {
        public InvalidShippingCostTypeException(String message) {
            super(message);
        }
    }

    public static class InvalidAdvancementPaymentException extends RuntimeException {
        public InvalidAdvancementPaymentException(String message) {
            super(message);
        }
    }
}
