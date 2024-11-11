package ru.t1.school.open.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.t1.school.open.project.entity.User;
import ru.t1.school.open.project.repo.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(@NonNull User user) {
        return userRepository.save(user);
    }

    public User getById(@NonNull String id) {
        return userRepository.getReferenceById(UUID.fromString(id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User change(@NonNull String id, @NonNull User user) {
        user.setId(UUID.fromString(id));
        return userRepository.save(user);
    }

    public void remove(@NonNull String id) {
        userRepository.deleteById(UUID.fromString(id));
    }
}
