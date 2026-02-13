package project.subscription.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.exception.ex.SubscriptionNotFoundException;
import project.subscription.exception.ex.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(CommonApiResponse.error(message));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonApiResponse<?>> userNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(404).body(CommonApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<CommonApiResponse<?>> subscriptionNotFoundException(SubscriptionNotFoundException e) {
        return ResponseEntity.status(404).body(CommonApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonApiResponse<?>> accessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(403).body(CommonApiResponse.error(e.getMessage()));
    }
}
