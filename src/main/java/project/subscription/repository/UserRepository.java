package project.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.subscription.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUserKey(String userKey);
}
