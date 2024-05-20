package io.github.honoriuss.tracking;

import io.github.honoriuss.tracking.annotations.TrackParameters;
import io.github.honoriuss.tracking.interfaces.ITrackingHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
class TrackingAspect { //TODO null checks

    private final ITrackingHandler trackingHandler;
    private final TrackingAspectHelper trackingAspectHelper;

    public TrackingAspect(ITrackingHandler trackingHandler, TrackingProperties trackingProperties) {
        this.trackingHandler = trackingHandler;
        this.trackingAspectHelper = new TrackingAspectHelper(trackingProperties);
    }

    @Before("@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        var message = createMessageString(joinPoint, trackParameters.parameterNames());
        trackingHandler.handleTracking(message);
    }

    @AfterReturning(value = "@annotation(io.github.honoriuss.tracking.annotations.TrackResult)", returning = "result")
    void track(Object result) {
        var resultJson = trackingAspectHelper.createMessageString(result);
        trackingHandler.handleTracking(resultJson);
    }

    private String createMessageString(JoinPoint joinPoint, String[] parameterNames) {
        var args = joinPoint.getArgs();
        return trackingAspectHelper.createMessageString(args, parameterNames);
    }
}
