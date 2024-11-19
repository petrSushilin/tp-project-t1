package ru.t1.school.open.project.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.t1.school.open.project.entity.Task;
import ru.t1.school.open.project.global.exception.TaskValidationException;

import java.util.Arrays;

@Aspect
@Component
public class InputTaskValidationAspect {
    @Pointcut("execution(* ru.t1.school.open.project.service.TaskService.create(..))")
    public void validateInputTask() {
    }

    @Before("@annotation(ru.t1.school.open.project.aspect.annotation.Validatable)")
    public void validateType(JoinPoint joinPoint) {
        Task incoming = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Task)
                .map(arg -> (Task) arg)
                .findFirst()
                // Будем прерывать работу исключая NPE, для базовых решений есть аннотация @NonNull
                .orElseThrow(() -> new TaskValidationException("Task is required"));

        if (incoming.getDescription() == null || incoming.getDescription().isEmpty()) {
            throw new TaskValidationException("Description cannot be empty");
        }

        if (incoming.getTitle() == null || !incoming.getTitle().startsWith("Task#")) {
            throw new TaskValidationException("Incorrect Task Title format.");
        }
    }
}
