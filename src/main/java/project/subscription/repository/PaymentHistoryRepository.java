package project.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.subscription.entity.PaymentHistory;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query("select sum(p.price) from PaymentHistory p where p.user = :user and  p.paymentMonth = :month")
    Optional<Long> sumByPaymentMonth(User user, int month);

    @Query("select count(p) from PaymentHistory p where p.user = :user  and p.paymentMonth = :month")
    int countByPaymentMonth(User user, int month);

    PaymentHistory findByUserAndSubscription(User user, Subscription subscription);
}
