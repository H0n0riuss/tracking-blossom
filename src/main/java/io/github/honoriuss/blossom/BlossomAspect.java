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
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;

@Aspect
@Component
class BlossomAspect<T> { //TODO null checks
    private final ITrackingHandler<T> trackingHandler;
    private final ITrackingObjectMapper<T> trackingObjectMapper;
    private final BlossomAspectHelper<T> blossomAspectHelper;

    public BlossomAspect(ITrackingHandler<T> trackingHandler,
                         ITrackingObjectMapper<T> trackingObjectMapper,
                         BlossomAspectHelper<T> blossomAspectHelper) {
        this.trackingHandler = trackingHandler;
        this.trackingObjectMapper = trackingObjectMapper;
        this.blossomAspectHelper = blossomAspectHelper;
    }

    @Before(value = "@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        trackingHandler.handleTracking(blossomAspectHelper.createTrackingObject(joinPoint, trackParameters));
    }

    @AfterReturning(value = "@annotation(io.github.honoriuss.blossom.annotations.TrackResult)", returning = "result")
    void track(Object result) {
        trackingHandler.handleTracking(trackingObjectMapper.mapResult(result));
    }
}

@Service
class BlossomAspectHelper<T> {
    private final Logger logger = LoggerFactory.getLogger(BlossomAspectHelper.class);

    private final ITrackingObjectMapper<T> trackingObjectMapper;
    BlossomAspectHelper(ITrackingHandler<T> trackingHandler,
                        ITrackingObjectMapper<T> trackingObjectMapper) {
        this.trackingObjectMapper = trackingObjectMapper;
        compareGenericParams(trackingHandler, trackingObjectMapper); //TODO
        logger.info("using ITrackingHandler: {} and ITrackingObjectMapper: {}", trackingHandler.getClass(), trackingObjectMapper.getClass());
    }

    protected void addOptionalArgument(ArrayList<Object> args, ArrayList<String> parameterNames, Object optArg, String optKey) {
        if (!optKey.isEmpty()) {
            args.add(optArg);
            parameterNames.add(optKey);
        }
    }

    protected void addAppContextArgument(ArrayList<Object> args, ArrayList<String> parameterNames, AppContext appContext) {
        if (appContext != null) {
            args.add(appContext.app());
            parameterNames.add(appContext.appKey());
        }
    }

    protected void compareGenericParams(ITrackingHandler<T> trackingHandler,
                                        ITrackingObjectMapper<T> trackingObjectMapper) {
        var handlerType = ((ParameterizedType) trackingHandler.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        var mapperType = ((ParameterizedType) trackingObjectMapper.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        if (!handlerType.equals(mapperType)) {
            logger.info("Generic types from handler ({}) and mapper({}) are maybe not compatible", handlerType, mapperType);
        }
    }

    protected AppContext getOptionalAppContext(JoinPoint joinPoint) {
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

    protected T createTrackingObject(JoinPoint joinPoint, TrackParameters trackParameters) {
        var args = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        var parameterNames = new ArrayList<>(Arrays.asList(trackParameters.parameterNames()));

        addOptionalArgument(args, parameterNames, trackParameters.optArg(), trackParameters.optKey());
        addAppContextArgument(args, parameterNames, getOptionalAppContext(joinPoint));

        return trackingObjectMapper.mapParameters(args, parameterNames);
    }
}
