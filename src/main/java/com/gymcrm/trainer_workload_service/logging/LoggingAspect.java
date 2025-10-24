package com.gymcrm.trainer_workload_service.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.gymcrm.trainer_workload_service.controller.*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.gymcrm.trainer_workload_service.service.*.*(..))")
    public void serviceMethods() {}

    @Around("controllerMethods()")
    public Object logAroundControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAroundMethod(joinPoint, "Controller");
    }

    @Around("serviceMethods()")
    public Object logAroundServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAroundMethod(joinPoint, "Service");
    }

    private Object logAroundMethod(ProceedingJoinPoint joinPoint, String componentType) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String transactionId = MDC.get("transactionId");
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.debug("Transaction [{}] - {} operation - {}.{} - Started with arguments: {}",
                transactionId, componentType, className, methodName, maskedArgs(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logger.debug("Transaction [{}] - {} operation - {}.{} - Completed in {}ms with result: {}",
                    transactionId, componentType, className, methodName, executionTime, result);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            logger.error("Transaction [{}] - {} operation - {}.{} - Failed after {}ms with error: {}",
                    transactionId, componentType, className, methodName, executionTime, e.getMessage(), e);

            throw e;
        }
    }

    private String maskedArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }
                    if (arg.toString().contains("password")) {
                        return "********";
                    }
                    return arg.toString();
                })
                .reduce((a, b) -> a + ", " + b)
                .map(s -> "[" + s + "]")
                .orElse("[]");
    }
}