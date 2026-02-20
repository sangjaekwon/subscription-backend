package project.subscription.service;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Cacheable(
            value = "subscriptions:due",
            key = "#userId"
    )
    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptionsDueSoon(Long userId, int day) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Subscription> subscriptionDue =
                subscriptionRepository.findSubscriptionDue(user, LocalDate.now().plusDays(day));


        return subscriptionDue.stream()
                .map(SubscriptionDto::new)
                .toList();
    }


    @Cacheable(
            value = "subscriptions",
            key = "#userId"
    )
    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptions(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Subscription> subscriptionList = subscriptionRepository.findByUser(user);

        return subscriptionList.stream()
                .map(SubscriptionDto::new)
                .toList();
    }

    @Caching(evict = {
            @CacheEvict(value = "subscriptions", key = "#userId"),
            @CacheEvict(value = "subscriptions:due", key = "#userId")
    })
    public void saveSubscription(SubscriptionDto subscriptionDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        CycleType cycle = subscriptionDto.getPaymentCycle();

        LocalDate time = cycle.plus(subscriptionDto.getDday(), subscriptionDto.getCycleInterval());

        time = calculateNextPayDate(subscriptionDto, time);

        List<Integer> alarmList = subscriptionDto.getAlarm();
        List<LocalDate> alarm = calculateAlarms(alarmList, time);

        Subscription subscription = new Subscription(subscriptionDto.getCategory(), subscriptionDto.getName(),
                subscriptionDto.getPaymentCycle(), subscriptionDto.getCycleInterval(), time, subscriptionDto.getPrice(),
                subscriptionDto.getAlarm(), alarm);

        user.addSubscription(subscription);
    }


    @Caching(evict = {
            @CacheEvict(value = "subscriptions", key = "#userId"),
            @CacheEvict(value = "subscriptions:due", key = "#userId")
    })
    public void updateSubscription(SubscriptionDto subscriptionDto, Long userId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(SubscriptionNotFoundException::new);

        if (!subscription.getUser().getId().equals(userId)) throw new AccessDeniedException("해당 구독 정보에 접근할 수 없습니다.");

        CycleType cycle = subscriptionDto.getPaymentCycle();

        LocalDate time = cycle.plus(subscriptionDto.getDday(), subscriptionDto.getCycleInterval());

        time = calculateNextPayDate(subscriptionDto, time);

        List<Integer> alarmList = subscriptionDto.getAlarm();
        List<LocalDate> alarm = calculateAlarms(alarmList, time);

        subscription.updateSubscription(subscriptionDto.getCategory(), subscriptionDto.getName(),
                subscriptionDto.getPaymentCycle(), subscriptionDto.getCycleInterval(), time, subscriptionDto.getPrice(),
                subscriptionDto.getAlarm(), alarm);
    }

    @Caching(evict = {
            @CacheEvict(value = "subscriptions", key = "#userId"),
            @CacheEvict(value = "subscriptions:due", key = "#userId")
    })
    public void deleteSubscription(Long userId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(SubscriptionNotFoundException::new);

        if (!subscription.getUser().getId().equals(userId)) throw new AccessDeniedException("해당 구독 정보에 접근할 수 없습니다.");

        User user = subscription.getUser();
        user.removeSubscription(subscription);

    }


    private static LocalDate calculateNextPayDate(SubscriptionDto subscriptionDto, LocalDate time) {
        int lastDay = time.lengthOfMonth();
        time = time.withDayOfMonth(
                Math.min(subscriptionDto.getDday().getDayOfMonth(), lastDay)
        );
        return time;
    }

    private static List<LocalDate> calculateAlarms(List<Integer> alarmList, LocalDate time) {
        List<LocalDate> alarm = new ArrayList<>();
        if (!alarmList.isEmpty()) {
            for (Integer i : alarmList) {
                alarm.add(time.minusDays(i));
            }
        }
        return alarm;
    }

}
