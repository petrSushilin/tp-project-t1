package ru.t1.school.open.project.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import ru.t1.school.open.project.entity.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
@Component
@EnableAspectJAutoProxy
public class TaskAspect {
    @Pointcut("within(ru.t1.school.open.project..*)")
    public void taskAspect() {

    }

    @Before("@annotation(ru.t1.school.open.project.aspect.annotation.Changeable)")
    public void setUpChangeableMark(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Task)
                .map(arg -> (Task) arg)
                .forEach(task -> {
                    task.setDescription("CHANGED at: "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                            + task.getDescription());
                    System.out.println("Task with ID: " + task.getId() + " has been changed.");
                });
    }

}
