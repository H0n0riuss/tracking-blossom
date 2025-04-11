package io.github.honoriuss.blossom.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AClassUtils {

    public static boolean isReactive(ProceedingJoinPoint joinPoint) {
        var signature = (MethodSignature) joinPoint.getSignature();
        var method = signature.getMethod();

        var returnType = method.getReturnType();
        return (Mono.class.isAssignableFrom(returnType) || Flux.class.isAssignableFrom(returnType));
    }
}
