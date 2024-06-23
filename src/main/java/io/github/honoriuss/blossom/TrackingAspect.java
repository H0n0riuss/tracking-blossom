package io.github.honoriuss.blossom;

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
        compareGenericParams(trackingHandler, trackingObjectMapper); //TODO
        logger.info("using ITrackingHandler: {} and ITrackingObjectMapper: {}", this.trackingHandler.getClass(), this.trackingObjectMapper.getClass());
    }

    @Before("@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        var trackingObject = createTrackingObject(joinPoint, trackParameters.parameterNames());
        trackingHandler.handleTracking(trackingObject);
    }

    @AfterReturning(value = "@annotation(io.github.honoriuss.blossom.annotations.TrackResult)", returning = "result")
    void track(Object result) {
        var resultJson = trackingObjectMapper.mapResult(result);
        trackingHandler.handleTracking(resultJson);
    }

    private T createTrackingObject(JoinPoint joinPoint, String[] parameterNames) {
        var args = joinPoint.getArgs();
        return trackingObjectMapper.mapParameters(args, parameterNames);
    }

    private void compareGenericParams(ITrackingHandler<T> trackingHandler,
                                      ITrackingObjectMapper<T> trackingObjectMapper) {
        var handlerType = ((ParameterizedType) trackingHandler.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        var mapperType = ((ParameterizedType) trackingObjectMapper.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        logger.info("Generic types from handler ({}) and  mapper({}) are compatible: {}", handlerType, mapperType, handlerType.equals(mapperType));
    }
}
