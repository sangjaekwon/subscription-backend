package project.subscription.exception.ex;

public class ExpiredEmailCodeException extends BusinessException {
    public ExpiredEmailCodeException() {
        super(ErrorCode.EXPIRED_EMAILCODE);
    }
}
