package project.subscription.exception.ex;

public class BadLoginException extends BusinessException {
    public BadLoginException() {
        super(ErrorCode.BAD_LOGIN);
    }
}
