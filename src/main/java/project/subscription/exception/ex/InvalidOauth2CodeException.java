package project.subscription.exception.ex;

public class InvalidOauth2CodeException  extends BusinessException {
    public InvalidOauth2CodeException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
