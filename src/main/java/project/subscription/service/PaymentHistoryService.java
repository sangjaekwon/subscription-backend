package project.subscription.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.response.PaymentHistroyResponse;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.exception.ex.SubscriptionNotFoundException;
import project.subscription.exception.ex.UserNotFoundException;
import project.subscription.repository.PaymentHistoryRepository;
import project.subscription.repository.SubscriptionRepository;
import project.subscription.repository.UserRepository;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public PaymentHistroyResponse totalMoney(Long userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        int month = date.getMonthValue();
        Long totalPrice = paymentHistoryRepository.sumByPaymentMonth(user, month).orElse(0L);
        Long lastTotalPrice = paymentHistoryRepository.sumByPaymentMonth(user, month-1).orElse(0L);
        Long percentage;
        if(lastTotalPrice == 0) {
            percentage = 0L;
        } else {
            percentage = (long) (((totalPrice + 0.0) / lastTotalPrice - 1) * 100);
        }
        return new PaymentHistroyResponse(totalPrice, percentage);
    }

    public int totalCount(Long userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return paymentHistoryRepository.countByPaymentMonth(user, date.getMonthValue());
    }

}
