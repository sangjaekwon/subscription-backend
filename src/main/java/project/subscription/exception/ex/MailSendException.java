package project.subscription.exception.ex;

public class MailSendException extends BusinessException {
    public MailSendException() {
        super(ErrorCode.MAIL_SEND_FAILED);
    }
}
