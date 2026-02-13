package project.subscription.exception.ex;

public class SubscriptionNotFoundException extends BusinessException {
    public SubscriptionNotFoundException() {
        super(ErrorCode.SUBSCRIPTION_NOT_FOUND);
    }
}
