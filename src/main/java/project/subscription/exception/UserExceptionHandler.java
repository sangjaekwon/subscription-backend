package project.subscription.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.subscription.controller.UserController;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.exception.ex.BadLoginException;
import project.subscription.exception.ex.DuplicateUsernameException;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionHandler {


    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<CommonApiResponse<?>> duplicateUsernameException(DuplicateUsernameException e) {
        return ResponseEntity.status(400).body(CommonApiResponse.error(e.getMessage()));
    }
}
