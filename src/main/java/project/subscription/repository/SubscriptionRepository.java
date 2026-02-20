package project.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> user(User user);

    List<Subscription> findByUser(User user);

    List<Subscription> findByAlarmDateContaining(LocalDate now);

    @Query(value = "select s from Subscription s where s.dday <= :date and s.user = :user order by s.dday ")
    List<Subscription> findSubscriptionDue(User user, LocalDate date);
}
