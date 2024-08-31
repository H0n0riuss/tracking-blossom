package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.annotations.AppContext;
import io.github.honoriuss.blossom.annotations.TrackParameters;
import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;

@Aspect
@Component
class BlossomAspect<T> { //TODO null checks
    private final Logger logger = LoggerFactory.getLogger(BlossomAspect.class);

    private final ITrackingHandler<T> trackingHandler;
    private final ITrackingObjectMapper<T> trackingObjectMapper;

    public BlossomAspect(ITrackingHandler<T> trackingHandler,
                         ITrackingObjectMapper<T> trackingObjectMapper) {
        this.trackingHandler = trackingHandler;
        this.trackingObjectMapper = trackingObjectMapper;
        compareGenericParams(trackingHandler, trackingObjectMapper); //TODO
        logger.info("using ITrackingHandler: {} and ITrackingObjectMapper: {}", this.trackingHandler.getClass(), this.trackingObjectMapper.getClass());
    }

    @Before(value = "@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        trackingHandler.handleTracking(createTrackingObject(joinPoint, trackParameters));
    }

    @AfterReturning(value = "@annotation(io.github.honoriuss.blossom.annotations.TrackResult)", returning = "result")
    void track(Object result) {
        trackingHandler.handleTracking(trackingObjectMapper.mapResult(result));
    }

    private T createTrackingObject(JoinPoint joinPoint, TrackParameters trackParameters) {
        var args = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        var parameterNames = new ArrayList<>(Arrays.asList(trackParameters.parameterNames()));

        addOptionalArgument(args, parameterNames, trackParameters.optArg(), trackParameters.optKey());
        addAppContextArgument(args, parameterNames, getOptionalAppContext(joinPoint));

        return trackingObjectMapper.mapParameters(args, parameterNames);
    }

    private void addOptionalArgument(ArrayList<Object> args, ArrayList<String> parameterNames, Object optArg, String optKey) {
        if (!optKey.isEmpty()) {
            args.add(optArg);
            parameterNames.add(optKey);
        }
    }

    private void addAppContextArgument(ArrayList<Object> args, ArrayList<String> parameterNames, AppContext appContext) {
        if (appContext != null) {
            args.add(appContext.app());
            parameterNames.add(appContext.appKey());
        }
    }

    private void compareGenericParams(ITrackingHandler<T> trackingHandler,
                                      ITrackingObjectMapper<T> trackingObjectMapper) {
        var handlerType = ((ParameterizedType) trackingHandler.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        var mapperType = ((ParameterizedType) trackingObjectMapper.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        if (!handlerType.equals(mapperType)) {
            logger.info("Generic types from handler ({}) and mapper({}) are maybe not compatible", handlerType, mapperType);
        }
    }

    private AppContext getOptionalAppContext(JoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (targetClass.isAnnotationPresent(AppContext.class)) {
            return targetClass.getAnnotation(AppContext.class);
        }

        try { //TODO check if needed --> cause user has bad architecture design?
            String methodName = joinPoint.getSignature().getName();
            Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
            if (targetClass.getMethod(methodName, parameterTypes).isAnnotationPresent(AppContext.class)) {
                return targetClass.getMethod(methodName, parameterTypes).getAnnotation(AppContext.class);
            }
        } catch (NoSuchMethodException e) {
            logger.debug(e.getMessage());
        }

        return null;
    }
}
