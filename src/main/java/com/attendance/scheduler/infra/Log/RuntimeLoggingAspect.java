package com.attendance.scheduler.infra.Log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RuntimeLoggingAspect {


    @Order(2)
    @Around("execution(* com.attendance.scheduler..*Controller.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String layer = joinPoint.getTarget().getClass().getSimpleName();
        String location = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        try {
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long runtime = endTime - startTime;

            log.info(location);
            log.info(layer+ " - " + methodName+ " - " + runtime + "ms" + '\n');

            return result;
        }catch(Throwable e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.attendance.scheduler.*.application.*.*(..))")
    public Object logApplication(ProceedingJoinPoint joinPoint) throws Throwable {
        String layer = joinPoint.getTarget().getClass().getSimpleName();
        String location = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;

        log.info(location);
        log.info(layer+ " - " + methodName+ " - " + runtime + "ms" + '\n');

        return result;
    }

    @Around("execution(* com.attendance.scheduler.*.repository.*.*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        String layer = joinPoint.getTarget().getClass().getSimpleName();
        String location = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long runtime = endTime - startTime;

        log.info(location);
        log.info(layer+ " - " + methodName+ " - " + runtime + "ms" + '\n');

        return result;
    }
}
