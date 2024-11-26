package ru.t1.school.open.project.application.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import ru.t1.school.open.project.domain.entity.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
@Component
@EnableAspectJAutoProxy
public class TaskEntityChangeableAspect {
    /*
    В целом, данный аспект нарушает принципы работы с AOP - внутри себя содержит бизнес-логику,
    а также содержит в себе помимо работы с изменением сущности еще и benchmark, однако, я реализовал это,
    чтобы отработать возможность такого функционала...
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskEntityChangeableAspect.class);

    // Перехватываем только для Task аннотированными @Changeable
    @Pointcut("execution(* *(.., ru.t1.school.open.project.domain.entity.Task, ..)) && @annotation(ru.t1.school.open.project.application.aspect.annotation.Changeable)")
    public void changeTaskAnnotated() {
    }

    @Before("changeTaskAnnotated()")
    public void setUpChangeableMark(JoinPoint joinPoint) {
        // По логике в данном аспекте выполнялось бы заполнение поля changedAt
        Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof Task)
                .map(arg -> (Task) arg)
                /*
                Как альтернатива можно было бы сделать в Changeable поле type() куда бы мы передавали тип класса
                затем бы мы реализовали Chain of responsibility с хэндлерами для каждого типа и так мы бы достигли случая,
                когда на аннотацию @Changeable регистрируется изменение для каждого класса уникально:
                 - для String аналогично ниже;
                 - для какого-то класса заполнение поля changedAt;
                 - для прочих иное поведение.
                */
                .forEach(task -> {
                    task.setDescription("CHANGED at: "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                            + "; " + task.getDescription());
                });
    }

    @Around("changeTaskAnnotated()")
    public Object loggingChange(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        logger.info("Task change executed in {} ms", endTime - startTime);
        return result;
    }
}
