package ru.t1.school.open.project.application.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.t1.school.open.project.api.dto.UserDto;
import ru.t1.school.open.project.application.util.mapper.UserMapper;
import ru.t1.school.open.project.domain.entity.Role;
import ru.t1.school.open.project.domain.entity.User;
import ru.t1.school.open.project.domain.enums.UserRoles;
import ru.t1.school.open.project.repo.RoleRepository;
import ru.t1.school.open.project.repo.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserDto create(UserDto userDto) {
        return Stream.of(userDto)
                .map(this::convertToEntity)
                .map(userRepository::save)
                .map(UserMapper::toDto)
                .findFirst()
                .orElseThrow();
    }

    public UserDto getById(Long id) {
        return userRepository
                .findById(id)
                .map(UserMapper::toDto)
                .orElseThrow();
    }

    public List<UserDto> getAll() {
        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserDto change(@NonNull Long id, UserDto userDto) {
        userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User changedUser = convertToEntity(userDto);
        changedUser.setId(id);

        return Stream.of(changedUser)
                .map(userRepository::save)
                .map(UserMapper::toDto)
                .findFirst()
                .orElseThrow();
    }

    public void remove(@NonNull String id) {
        userRepository.deleteById(Long.parseLong(id));
    }

    private User convertToEntity(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);

        Set<Role> roles = userDto.roles().stream()
                .map(roleRepository::findByName)
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return user;
    }

    public boolean existingByUsername(String username) {
        return userRepository.existingByUsername(username);
    }
}
