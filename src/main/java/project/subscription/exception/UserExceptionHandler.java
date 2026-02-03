package project.subscription.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.subscription.controller.UserController;
import project.subscription.exception.ex.BadLoginException;
import project.subscription.exception.ex.DuplicateUsernameException;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> usernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<String> duplicateUsernameException(DuplicateUsernameException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
    @ExceptionHandler(BadLoginException.class)
    public ResponseEntity<String> badLoginException(BadLoginException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }
}
