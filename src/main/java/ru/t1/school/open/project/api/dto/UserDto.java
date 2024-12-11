package ru.t1.school.open.project.api.dto;

import ru.t1.school.open.project.domain.enums.UserRoles;

import java.util.Set;

public record UserDto (long id, String username, String password, Set<UserRoles> roles) {
}
