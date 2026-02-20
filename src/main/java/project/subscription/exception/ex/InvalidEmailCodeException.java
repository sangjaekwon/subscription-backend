package project.subscription.exception.ex;

public class InvalidEmailCodeException extends BusinessException {
    public InvalidEmailCodeException() {
        super(ErrorCode.INVALID_EMAILCODE);
    }
}
