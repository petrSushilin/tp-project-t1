package ru.t1.school.open.project.api.dto;

import ru.t1.school.open.project.domain.enums.TaskStatus;

public record TaskDto (long id, String title, String description, TaskStatus status, long userId) {
}
