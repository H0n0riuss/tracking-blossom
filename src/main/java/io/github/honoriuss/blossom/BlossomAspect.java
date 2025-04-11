package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.annotations.AdditionalTrackingInfo;
import io.github.honoriuss.blossom.annotations.AppContext;
import io.github.honoriuss.blossom.annotations.Track;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static io.github.honoriuss.blossom.utils.AClassUtils.isReactive;

@Aspect
@Component
class BlossomAspect<T> { //TODO null checks
    private final ITrackingHandler<T> trackingHandler;
    private final ITrackingObjectMapper<T> trackingObjectMapper;
    private final BlossomAspectHelper<T> blossomAspectHelper;
    private final BlossomReactiveHelper<T> blossomReactiveHelper;

    public BlossomAspect(ITrackingHandler<T> trackingHandler,
                         ITrackingObjectMapper<T> trackingObjectMapper,
                         BlossomAspectHelper<T> blossomAspectHelper,
                         BlossomReactiveHelper<T> blossomReactiveHelper) {
        this.trackingHandler = trackingHandler;
        this.trackingObjectMapper = trackingObjectMapper;
        this.blossomAspectHelper = blossomAspectHelper;
        this.blossomReactiveHelper = blossomReactiveHelper;
    }

    @Before(value = "@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        var trackingObj = blossomAspectHelper.createTrackingObject(joinPoint, Arrays.asList(trackParameters.parameterNames()), trackParameters.optKey(), trackParameters.optArg());

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

    @Around("@annotation(track)")
    public Object trackInputAndOutput(ProceedingJoinPoint joinPoint, Track track) throws Throwable {
        if (isReactive(joinPoint)) {
            return blossomReactiveHelper.handleReactiveStack(joinPoint, track);
        }
        Object result = joinPoint.proceed();
        var trackingObj = blossomAspectHelper.createTrackingObject(joinPoint, Arrays.asList(track.parameterNames()), track.optKey(), track.optArg(), track.returnName(), result);

        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.isAnnotationPresent(AdditionalTrackingInfo.class)) {
            trackingHandler.handleTracking(trackingObj, method.getAnnotation(AdditionalTrackingInfo.class));
        } else {
            trackingHandler.handleTracking(trackingObj);
        }

        return result;
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

    protected void addOptionalArgument(List<Object> args, List<String> parameterNames, Object optArg, String optKey) {
        if (!optKey.isEmpty()) {
            args.add(optArg);
            parameterNames.add(optKey);
        }
    }

    protected void addAppContextArgument(List<Object> args, List<String> parameterNames, AppContext appContext) {
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

    protected T createTrackingObject(JoinPoint joinPoint, List<String> parameterNames, String optKey, String optArg) {
        return createTrackingObject(joinPoint, parameterNames, optKey, optArg, "", null);
    }

    protected T createTrackingObject(JoinPoint joinPoint, List<String> parameterNames, String optKey, String optArg,
                                     String resultName, Object result) {
        var args = Arrays.asList(joinPoint.getArgs());

        addOptionalArgument(args, parameterNames, optArg, optKey);
        addAppContextArgument(args, parameterNames, getOptionalAppContext(joinPoint));

        if (resultName != null &&
                !resultName.isEmpty() &&
                result != null) {
            addResultArgument(resultName, result);
        }

        return trackingObjectMapper.mapParameters(args, parameterNames);
    }

    private void addResultArgument(String resultName, Object result) { //TODO

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

@Service
class BlossomReactiveHelper<T> {
    protected Object handleReactiveStack(ProceedingJoinPoint joinPoint, Track track) throws Throwable {
        var result = joinPoint.proceed();
        if (result instanceof Mono) {
            return ((Mono<?>) result)
                    .doOnNext(value -> {
                        System.out.println("📤 Rückgabe (Mono): " + value);
                    })
                    .doOnTerminate(() -> {
                        System.out.println("✅ [Mono] Methode abgeschlossen: ");
                    });

        } else if (result instanceof Flux) {
            return ((Flux<?>) result)
                    .doOnNext(value -> {
                        System.out.println("📤 Rückgabe (Flux): " + value);
                    })
                    .doOnComplete(() -> {
                        System.out.println("✅ [Flux] Methode abgeschlossen: ");
                    });

        }
        throw new IllegalArgumentException("Cant handle reactive stack...");
    }
}
