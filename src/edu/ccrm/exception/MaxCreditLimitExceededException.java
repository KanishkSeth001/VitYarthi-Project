package edu.ccrm.exception;

public class MaxCreditLimitExceededException extends Exception {
    public MaxCreditLimitExceededException(String message) {
        super(message);
    }
    
    public MaxCreditLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}