package project.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.subscription.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
