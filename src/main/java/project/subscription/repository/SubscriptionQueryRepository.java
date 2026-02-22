package project.subscription.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.subscription.dto.SubscriptionDto;
import project.subscription.dto.request.SubscriptionSearchCondition;
import project.subscription.entity.User;

import java.time.LocalDate;

public interface SubscriptionQueryRepository {

    Page<SubscriptionDto> searchPageSubscriptionsByCondition
            (User user, SubscriptionSearchCondition condition, Pageable pageable);

    Page<SubscriptionDto> findPageSubscriptions(User user, Pageable pageable);

    Page<SubscriptionDto> findPageSubscriptionsDueSoon(User user, LocalDate date, Pageable pageable);

}
