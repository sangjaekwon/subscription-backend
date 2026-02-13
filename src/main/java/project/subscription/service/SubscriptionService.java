package project.subscription.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.SubscriptionDto;
import project.subscription.entity.CycleType;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.exception.ex.SubscriptionNotFoundException;
import project.subscription.exception.ex.UserNotFoundException;
import project.subscription.repository.SubscriptionRepository;
import project.subscription.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Subscription> subscriptionList = subscriptionRepository.findByUser(user);

        return subscriptionList.stream()
                .map(SubscriptionDto::new)
                .toList();
    }


    public void saveSubscription(SubscriptionDto subscriptionDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        CycleType cycle = subscriptionDto.getPaymentCycle();

        LocalDate time = subscriptionDto.getDday();
        if (cycle.equals(CycleType.MONTH)) {
            time = time.plusMonths(subscriptionDto.getCycleInterval());

        } else {
            time = time.plusYears(subscriptionDto.getCycleInterval());
        }
        int lastDay = time.lengthOfMonth();
        time = time.withDayOfMonth(
                Math.min(subscriptionDto.getDday().getDayOfMonth(), lastDay)
        );

        List<Integer> alarmList = subscriptionDto.getAlarm();
        List<LocalDate> alarm = new ArrayList<>();
        if (!alarmList.isEmpty()) {
            for (Integer i : alarmList) {
                alarm.add(time.minusDays(i));
            }
        }

        Subscription subscription = new Subscription(subscriptionDto.getCategory(), subscriptionDto.getName(),
                subscriptionDto.getPaymentCycle(), subscriptionDto.getCycleInterval(), time, subscriptionDto.getPrice(),
                subscriptionDto.getAlarm(), alarm);

        user.addSubscription(subscription);
    }


    public void updateSubscription(SubscriptionDto subscriptionDto, Long userId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(SubscriptionNotFoundException::new);

        if (!subscription.getUser().getId().equals(userId)) throw new AccessDeniedException("해당 구독 정보에 접근할 수 없습니다.");

        CycleType cycle = subscriptionDto.getPaymentCycle();

        LocalDate time = subscriptionDto.getDday();
        if (cycle.equals(CycleType.MONTH)) {
            time = time.plusMonths(subscriptionDto.getCycleInterval());

        } else {
            time = time.plusYears(subscriptionDto.getCycleInterval());
        }
        int lastDay = time.lengthOfMonth();
        time = time.withDayOfMonth(
                Math.min(subscriptionDto.getDday().getDayOfMonth(), lastDay)
        );

        List<Integer> alarmList = subscriptionDto.getAlarm();
        List<LocalDate> alarm = new ArrayList<>();
        if (!alarmList.isEmpty()) {
            for (Integer i : alarmList) {
                alarm.add(time.minusDays(i));
            }
        }

        subscription.updateSubscription(subscriptionDto.getCategory(), subscriptionDto.getName(),
                subscriptionDto.getPaymentCycle(), subscriptionDto.getCycleInterval(), time, subscriptionDto.getPrice(),
                subscriptionDto.getAlarm(), alarm);
    }


    public void deleteSubscription(Long userId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(SubscriptionNotFoundException::new);

        if (!subscription.getUser().getId().equals(userId)) throw new AccessDeniedException("해당 구독 정보에 접근할 수 없습니다.");

        User user = subscription.getUser();
        user.removeSubscription(subscription);

    }

}
