package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.annotations.AdditionalTrackingInfo;
import io.github.honoriuss.blossom.annotations.AppContext;
import io.github.honoriuss.blossom.annotations.TrackParameters;
import io.github.honoriuss.blossom.interfaces.ITrackingAppContextHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
class BlossomAspect<T> { //TODO null checks
    private final ITrackingHandler<T> trackingHandler;
    private final ITrackingObjectMapper<T> trackingObjectMapper;
    private final BlossomAspectHelper<T> blossomAspectHelper;

    private final LoggingContext loggingContext;

    public BlossomAspect(ITrackingHandler<T> trackingHandler,
                         ITrackingObjectMapper<T> trackingObjectMapper,
                         BlossomAspectHelper<T> blossomAspectHelper, LoggingContext loggingContext) {
        this.trackingHandler = trackingHandler;
        this.trackingObjectMapper = trackingObjectMapper;
        this.blossomAspectHelper = blossomAspectHelper;
        this.loggingContext = loggingContext;
    }

    @Before(value = "@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        var trackingObj = blossomAspectHelper.createTrackingObject(joinPoint, trackParameters);

        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.isAnnotationPresent(AdditionalTrackingInfo.class)) {
            trackingHandler.handleTracking(trackingObj, method.getAnnotation(AdditionalTrackingInfo.class));
        } else {
            trackingHandler.handleTracking(trackingObj);
        }
    }

    @AfterReturning(value = "@annotation(io.github.honoriuss.blossom.annotations.TrackResult)", returning = "result")
    void track(Object result) {
        trackingHandler.handleTracking(trackingObjectMapper.mapResult(result));
    }

    @Around("@annotation(io.github.honoriuss.blossom.annotations.TrackParamsInContext)")
    Object logMethodParams(ProceedingJoinPoint joinPoint) throws Throwable {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var methodName = methodSignature.getName();
        var args = joinPoint.getArgs();

        var params = Arrays.stream(args)
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(", "));

        var logEntry = String.format("Method: %s, Params: [%s]", methodName, params);
        loggingContext.addLogEntry(logEntry);

        return joinPoint.proceed();
    }
}

@Service
class BlossomAspectHelper<T> {
    private final Logger logger = LoggerFactory.getLogger(BlossomAspectHelper.class);

    private final ITrackingObjectMapper<T> trackingObjectMapper;
    private final ITrackingAppContextHandler trackingAppContextHandler;

    BlossomAspectHelper(ITrackingHandler<T> trackingHandler,
                        ITrackingObjectMapper<T> trackingObjectMapper,
                        ITrackingAppContextHandler trackingAppContextHandler) {
        this.trackingObjectMapper = trackingObjectMapper;
        this.trackingAppContextHandler = trackingAppContextHandler;
        //compareGenericParams(trackingHandler, trackingObjectMapper); //TODO
        logger.info("using ITrackingHandler: {} and ITrackingObjectMapper: {}", trackingHandler.getClass(), trackingObjectMapper.getClass());
    }

    protected void addOptionalArgument(ArrayList<Object> args, ArrayList<String> parameterNames, Object optArg, String optKey) {
        if (!optKey.isEmpty()) {
            args.add(optArg);
            parameterNames.add(optKey);
        }
    }

    protected void addAppContextArgument(ArrayList<Object> args, ArrayList<String> parameterNames, AppContext appContext) {
        trackingAppContextHandler.addAppContext(args, parameterNames, appContext);
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

    protected void compareGenericParams(ITrackingHandler<T> trackingHandler,
                                        ITrackingObjectMapper<T> trackingObjectMapper) {
        try {
            var genericHandlerInterface = getGenericType(trackingHandler, ITrackingHandler.class);
            var genericMapperInterface = getGenericType(trackingObjectMapper, ITrackingObjectMapper.class);

            if (genericHandlerInterface instanceof ParameterizedType &&
                    genericMapperInterface instanceof ParameterizedType) {
                var handlerType = ((ParameterizedType) genericHandlerInterface).getActualTypeArguments()[0];
                var mapperType = ((ParameterizedType) genericMapperInterface).getActualTypeArguments()[0];
                if (!handlerType.equals(mapperType)) {
                    logger.info("Generic types from handler ({}) and mapper({}) are maybe not compatible", handlerType, mapperType);
                }
            } else {
                logger.info("Generic types from handler ({}) and mapper({}) are maybe not compatible", genericHandlerInterface, genericMapperInterface);
            }
        } catch (Exception ex) {
            logger.warn("Cant compare generic types: ", ex);
        }
    }

    private Type getGenericType(Object instance, Class<?> interfaceClass) {
        var genericInterfaces = instance.getClass().getGenericInterfaces();

        for (var genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                if (parameterizedType.getRawType().equals(interfaceClass)) {
                    return parameterizedType.getActualTypeArguments()[0];
                }
            }
        }

        return null;
    }
}

