package ru.t1.school.open.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.school.open.project.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String login);

    boolean existingByUsername(String username);
}
