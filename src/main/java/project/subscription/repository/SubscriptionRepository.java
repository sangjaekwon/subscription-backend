package project.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, SubscriptionQueryRepository {
    List<Subscription> user(User user);

    List<Subscription> findByUser(User user);

    @Query(value = "SELECT * FROM subscription WHERE FIND_IN_SET(:today, alarm_date) > 0", nativeQuery = true)
    List<Subscription> findByAlarmDateContaining(String today);


    List<Subscription> findByDday(LocalDate dday);
}
