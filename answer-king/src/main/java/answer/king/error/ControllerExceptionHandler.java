package answer.king.error;

import org.assertj.core.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    public static final String HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE = "Content received by server could not be read.";
    public static final String CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE = "The parameters set for this entity are invalid:\n";
    public static final String INPUT_VALIDATION_EXCEPTION_MESSAGE = "Validation error on the server:\n";
    public static final String RUNTIME_EXCEPTION_MESSAGE = "An unexpected error occurred on the server:\n";

    /**
     * Ran into a bit of an unexpected problem with error handling where requests that failed having come from the client
     * returned a different error to when the code was tested. I could not get to the bottom of this, other than it possibly
     * being related to the use of @Transactional annotation. After some though I concluded that it would make sense to
     * tighten up the error handling anyway, to look for root causes, as there could be some other strange scenario where
     * something like this could happen.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity handleException(RuntimeException e) {
        ResponseEntity re = handleGenericException(e);
        int statusCodeValue = re.getStatusCodeValue();
        if (statusCodeValue == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            Throwable t = Throwables.getRootCause(e);
            return handleGenericException(t);
        }
        return re;
    }

    private ResponseEntity handleGenericException(Throwable t) {
        if (t instanceof ConstraintViolationException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE + t.getMessage());
        }
        else if (t instanceof InputValidationException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(INPUT_VALIDATION_EXCEPTION_MESSAGE + t.getMessage());
        }
        else if (t instanceof HttpMessageNotReadableException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE);
        }
        else {
            logger.error("An unexpected error has occurred.", t);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RUNTIME_EXCEPTION_MESSAGE);
        }
    }
}