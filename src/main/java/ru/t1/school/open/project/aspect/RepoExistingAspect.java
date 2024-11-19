package ru.t1.school.open.project.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.t1.school.open.project.global.exception.RecordNotFoundException;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Aspect
@Component
public class RepoExistingAspect {
    private static final Logger logger = LoggerFactory.getLogger(RepoExistingAspect.class);

    @Pointcut("@annotation(ru.t1.school.open.project.aspect.annotation.Existing)")
    public void existingMethods() {
    }

    @Before("existingMethods()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Invoking method: {}", joinPoint.getSignature().toString());
    }

    @AfterReturning(pointcut = "existingMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Execute method: {}", joinPoint.getSignature().toString());
        if (result != null) {
            logger.info("Returned value: {}", result.toString());
        } else {
            logger.warn("Returned null value from method: {}", joinPoint.getSignature().toString());
        }
    }

    @AfterThrowing(pointcut = "existingMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        logger.error("Method {} threw an exception: {}", joinPoint.getSignature().toString(), ex.getMessage());
        if (ex instanceof NoSuchElementException) {
            handleNoSuchException(joinPoint, ex);
        }
    }

    private void handleNoSuchException(JoinPoint joinPoint, Exception ex) throws RecordNotFoundException {
        String id = Arrays.stream(joinPoint.getArgs())
                .findFirst()
                .orElseThrow()
                .toString();
        throw new RecordNotFoundException("Record with id " + id + " not found");
    }
}
