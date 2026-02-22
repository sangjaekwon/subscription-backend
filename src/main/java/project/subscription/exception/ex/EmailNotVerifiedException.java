package project.subscription.exception.ex;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException() {
        super(ErrorCode.EMAIL_NOT_VERIFIED);
    }
}
