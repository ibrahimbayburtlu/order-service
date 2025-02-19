package com.getirCase.order_service.exception;

/**
 * Custom exception for handling errors related to the Discount Service.
 */
public class DiscountServiceException extends RuntimeException {

    // Default constructor
    public DiscountServiceException() {
        super("An error occurred while processing the discount service request.");
    }

    // Constructor with custom message
    public DiscountServiceException(String message) {
        super(message);
    }

    // Constructor with custom message and throwable cause
    public DiscountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
