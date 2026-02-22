package project.subscription.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.SubscriptionDto;
import project.subscription.dto.response.PageResponse;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.exception.ex.SubscriptionNotFoundException;
import project.subscription.exception.ex.UserNotFoundException;
import project.subscription.repository.SubscriptionRepository;
import project.subscription.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public PageResponse<SubscriptionDto> findSubscriptionsDueSoon(Long userId, int day, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Page<SubscriptionDto> page = subscriptionRepository.findPageSubscriptionsDueSoon(user, LocalDate.now().plusDays(day), pageable);

        return new PageResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(),
                page.getPageable().getPageSize(), page.getPageable().getPageNumber(), page.getNumberOfElements());
    }


    @Transactional(readOnly = true)
    public PageResponse<SubscriptionDto> findSubscriptions(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Page<SubscriptionDto> page = subscriptionRepository.findPageSubscriptions(user, pageable);

        return new PageResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(),
                page.getPageable().getPageSize(), page.getPageable().getPageNumber(), page.getNumberOfElements());
    }


    public void saveSubscription(SubscriptionDto subscriptionDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        LocalDate nextPaymentDay = subscriptionDto.getDday();
        while (!nextPaymentDay.isAfter(LocalDate.now())) {
            nextPaymentDay = subscriptionDto.getPaymentCycle().plus
                    (nextPaymentDay, subscriptionDto.getCycleInterval());
        }

        List<Integer> alarmList = subscriptionDto.getAlarm();
        Set<LocalDate> alarm = calculateAlarms(alarmList, nextPaymentDay);

        Subscription subscription = new Subscription(subscriptionDto.getCategory(), subscriptionDto.getName(),
                subscriptionDto.getPaymentCycle(), subscriptionDto.getCycleInterval(), nextPaymentDay, subscriptionDto.getPrice(),
                subscriptionDto.getAlarm(), alarm);

        user.addSubscription(subscription);
    }


    public void updateSubscription(SubscriptionDto subscriptionDto, Long userId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(SubscriptionNotFoundException::new);

        if (!subscription.getUser().getId().equals(userId)) throw new AccessDeniedException("해당 구독 정보에 접근할 수 없습니다.");


        LocalDate nextPaymentDay = subscriptionDto.getDday();
        while (!nextPaymentDay.isAfter(LocalDate.now())) {
            nextPaymentDay = subscriptionDto.getPaymentCycle().plus
                    (nextPaymentDay, subscriptionDto.getCycleInterval());
        }

        List<Integer> alarmList = subscriptionDto.getAlarm();
        Set<LocalDate> alarm = calculateAlarms(alarmList, nextPaymentDay);

        subscription.updateSubscription(subscriptionDto.getCategory(), subscriptionDto.getName(),
                subscriptionDto.getPaymentCycle(), subscriptionDto.getCycleInterval(), nextPaymentDay, subscriptionDto.getPrice(),
                subscriptionDto.getAlarm(), alarm);
    }


    public void deleteSubscription(Long userId, Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(SubscriptionNotFoundException::new);

        if (!subscription.getUser().getId().equals(userId)) throw new AccessDeniedException("해당 구독 정보에 접근할 수 없습니다.");

        User user = subscription.getUser();
        user.removeSubscription(subscription);

    }


    private Set<LocalDate> calculateAlarms(List<Integer> alarmList, LocalDate time) {
        Set<LocalDate> alarm = new HashSet<>();
        if (alarmList != null && !alarmList.isEmpty()) {
            for (Integer i : alarmList) {
                alarm.add(time.minusDays(i));
            }
        }
        return alarm;
    }

    public void refreshSubscriptionCycle() {
        LocalDate now = LocalDate.now();
        List<Subscription> ddayList = subscriptionRepository.findByDday(now);

        for (Subscription sub : ddayList) {
            LocalDate dday = sub.getPaymentCycle().plus(now, sub.getCycleInterval());

            List<Integer> alarmList = sub.getAlarm();
            Set<LocalDate> alarmDate = calculateAlarms(alarmList, dday);

            sub.refreshPaymentDay(dday, alarmDate);
        }
    }
}
