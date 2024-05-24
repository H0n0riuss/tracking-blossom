package io.github.honoriuss.tracking;

import io.github.honoriuss.tracking.annotations.TrackParameters;
import io.github.honoriuss.tracking.interfaces.ITrackingHandler;
import io.github.honoriuss.tracking.interfaces.ITrackingObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
class TrackingAspect<T> { //TODO null checks
    private final Logger logger = LoggerFactory.getLogger(TrackingAspect.class);

    private final ITrackingHandler<T> trackingHandler;
    private final ITrackingObjectMapper<T> trackingObjectMapper;

    public TrackingAspect(ITrackingHandler<T> trackingHandler,
                          ITrackingObjectMapper<T> trackingObjectMapper) {
        this.trackingHandler = trackingHandler;
        this.trackingObjectMapper = trackingObjectMapper;
        logger.info("using ITrackingHandler: {} and ITrackingObjectMapper: {}", this.trackingHandler.getClass(), this.trackingObjectMapper.getClass());
    }

    @Before("@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        var trackingObject = createTrackingObject(joinPoint, trackParameters.parameterNames());
        trackingHandler.handleTracking(trackingObject);
    }

    @AfterReturning(value = "@annotation(io.github.honoriuss.tracking.annotations.TrackResult)", returning = "result")
    void track(Object result) {
        var resultJson = trackingObjectMapper.mapResult(result);
        trackingHandler.handleTracking(resultJson);
    }

    private T createTrackingObject(JoinPoint joinPoint, String[] parameterNames) {
        var args = joinPoint.getArgs();
        return trackingObjectMapper.mapParameters(args, parameterNames);
    }
}
