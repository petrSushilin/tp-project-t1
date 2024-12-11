package ru.t1.school.open.project.api.dto;

import java.util.Set;

public record JwtResponse (String first, String second, Long id, String username, Set<String> roles) {
}
