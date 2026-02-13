package project.subscription.exception.ex;

public class ExpiredJwtTokenException extends BusinessException {
    public ExpiredJwtTokenException() {
        super(ErrorCode.EXPIRED_TOKEN);
    }
}
