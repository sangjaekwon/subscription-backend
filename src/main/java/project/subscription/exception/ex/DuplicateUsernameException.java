package project.subscription.exception.ex;

public class DuplicateUsernameException extends BusinessException {
    public DuplicateUsernameException() {
        super(ErrorCode.DUPLICATE_USERNAME);
    }
}
