package project.subscription.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.subscription.controller.AuthController;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.exception.ex.ExpiredJwtTokenException;
import project.subscription.exception.ex.InvalidJwtTokenException;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {

    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<CommonApiResponse<?>> expiredJwtTokenException(ExpiredJwtTokenException e) {
        return ResponseEntity.status(401).body(CommonApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<CommonApiResponse<?>> invalidJwtTokenException(InvalidJwtTokenException e) {
        return ResponseEntity.status(401).body(CommonApiResponse.error(e.getMessage()));
    }

}
