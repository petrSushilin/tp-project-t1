package ru.t1.school.open.project.application.util.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.t1.school.open.project.api.dto.UserDto;
import ru.t1.school.open.project.domain.entity.Role;
import ru.t1.school.open.project.domain.entity.User;
import ru.t1.school.open.project.domain.enums.UserRoles;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Class<UserDto> USER_DTO_CLASS = UserDto.class;
    private static final Class<User> USER_CLASS = User.class;

    public static UserDto toDto(User user) {
        UserDto userDto = mapper.convertValue(user, USER_DTO_CLASS);
        Set<UserRoles> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return new UserDto(userDto.id(), userDto.username(), null, roles);
    }

    public static User toEntity(UserDto userDto) {
        return mapper.convertValue(userDto, USER_CLASS);
    }
}
