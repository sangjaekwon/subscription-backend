package project.subscription.exception.ex;

public class InvalidJwtTokenException extends BusinessException{
    public InvalidJwtTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
