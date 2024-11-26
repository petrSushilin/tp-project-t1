package ru.t1.school.open.project.application.util.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.t1.school.open.project.api.dto.TaskDto;
import ru.t1.school.open.project.domain.entity.Task;

public class TaskMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Class<TaskDto> TASK_DTO_CLASS = TaskDto.class;
    private static final Class<Task> TASK_CLASS = Task.class;

    public static TaskDto toDto(Task task) {
        return mapper.convertValue(task, TASK_DTO_CLASS);
    }

    public static Task toEntity(TaskDto taskDto) {
        return mapper.convertValue(taskDto, TASK_CLASS);
    }
}
