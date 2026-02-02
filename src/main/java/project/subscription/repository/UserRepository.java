package project.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.subscription.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
