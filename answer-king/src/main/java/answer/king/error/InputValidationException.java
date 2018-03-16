package answer.king.error;

/**
 * Represents an expected exception that will be handled gracefully. Created this to help with flow control
 * as a means of suppressing know issues like bad requests
 */
public class InputValidationException extends RuntimeException {
    public InputValidationException(String errorMessage) {
        super(errorMessage);
    }
}