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

import java.util.ArrayList;
import java.util.List;

import static io.github.honoriuss.blossom.utils.AClassUtils.isReactive;

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
        var trackingObj = blossomAspectHelper.createTrackingObject(joinPoint, trackParameters.parameterNames(), trackParameters.optKey(), trackParameters.optArg());

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
            return blossomAspectHelper.handleReactiveTracking(joinPoint, track);
        }
        return blossomAspectHelper.handleTracking(joinPoint, track);
    }
}

@Service
class BlossomAspectHelper<T> {
    private final Logger logger = LoggerFactory.getLogger(BlossomAspectHelper.class);

    private final ITrackingObjectMapper<T> trackingObjectMapper;
    private final ITrackingAppContextHandler trackingAppContextHandler;
    private final ITrackingHandler<T> trackingHandler;

    BlossomAspectHelper(ITrackingHandler<T> trackingHandler,
                        ITrackingObjectMapper<T> trackingObjectMapper,
                        ITrackingAppContextHandler trackingAppContextHandler) {
        this.trackingHandler = trackingHandler;
        this.trackingObjectMapper = trackingObjectMapper;
        this.trackingAppContextHandler = trackingAppContextHandler;
        logger.info("using ITrackingHandler: {} and ITrackingObjectMapper: {}", trackingHandler.getClass(), trackingObjectMapper.getClass());
    }

    protected Object handleTracking(ProceedingJoinPoint joinPoint, Track track) throws Throwable {
        Object result = joinPoint.proceed();
        createTracking(joinPoint, track, result);

        return result;
    }

    protected Object handleReactiveTracking(ProceedingJoinPoint joinPoint, Track track) throws Throwable {
        var result = joinPoint.proceed();
        if (result instanceof Mono) {
            return ((Mono<?>) result)
                    .doOnNext(resValue -> createTracking(joinPoint, track, resValue));
        } else if (result instanceof Flux) {
            return ((Flux<?>) result)
                    .doOnNext(resValue -> createTracking(joinPoint, track, resValue));
        }
        throw new IllegalArgumentException("Cant handle reactive stack...");
    }

    private void createTracking(ProceedingJoinPoint joinPoint, Track track, Object result) {
        var trackingObj = createTrackingObject(joinPoint, track.parameterNames(), track.optKey(), track.optArg(), track.returnName(), result);

        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.isAnnotationPresent(AdditionalTrackingInfo.class)) {
            trackingHandler.handleTracking(trackingObj, method.getAnnotation(AdditionalTrackingInfo.class));
        } else {
            trackingHandler.handleTracking(trackingObj);
        }
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

    protected T createTrackingObject(JoinPoint joinPoint, String[] parameterNames, String optKey, String optArg) {
        return createTrackingObject(joinPoint, parameterNames, optKey, optArg, "", null);
    }

    protected T createTrackingObject(JoinPoint joinPoint, String[] parameterNames, String optKey, String optArg,
                                     String resultName, Object result) {
        var args = new ArrayList<>(List.of(joinPoint.getArgs()));
        var paramNames = new ArrayList<>(List.of(parameterNames));

        addOptionalArgument(args, paramNames, optArg, optKey);
        addAppContextArgument(args, paramNames, getOptionalAppContext(joinPoint));
        addResultArgument(args, paramNames, resultName, result);

        return trackingObjectMapper.mapParameters(args, paramNames);
    }

    private void addResultArgument(ArrayList<Object> args, ArrayList<String> parameterNames, String resultName, Object result) {
        if (resultName != null && !resultName.isEmpty() && result != null) {
            args.add(result);
            parameterNames.add(resultName);
        }
    }
}
