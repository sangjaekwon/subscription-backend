package project.subscription.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.subscription.controller.AuthController;
import project.subscription.controller.UserController;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.exception.ex.BadLoginException;
import project.subscription.exception.ex.ExpiredJwtTokenException;
import project.subscription.exception.ex.InvalidJwtTokenException;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CommonApiResponse<?>> usernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(401).body(CommonApiResponse.error(e.getMessage()));
    }
    @ExceptionHandler(BadLoginException.class)
    public ResponseEntity<CommonApiResponse<?>> badLoginException(BadLoginException e) {
        return ResponseEntity.status(401).body(CommonApiResponse.error(e.getMessage()));
    }
    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<CommonApiResponse<?>> expiredJwtTokenException(ExpiredJwtTokenException e) {
        return ResponseEntity.status(401).body(CommonApiResponse.error(e.getMessage()));
    }
    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<CommonApiResponse<?>> invalidJwtTokenException(InvalidJwtTokenException e) {
        return ResponseEntity.status(401).body(CommonApiResponse.error(e.getMessage()));
    }

}
