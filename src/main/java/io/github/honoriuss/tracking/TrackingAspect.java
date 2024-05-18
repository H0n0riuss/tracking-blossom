package io.github.honoriuss.tracking;

import io.github.honoriuss.tracking.annotations.TrackParameters;
import io.github.honoriuss.tracking.interfaces.ITrackingHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
class TrackingAspect {

    private final ITrackingHandler iTrackingHandler;
    private final TrackingAspectHelper trackingAspectHelper;

    public TrackingAspect(ITrackingHandler iTrackingHandler, TrackingAspectHelper trackingAspectHelper) {
        this.iTrackingHandler = iTrackingHandler;
        this.trackingAspectHelper = trackingAspectHelper;
    }

    @Before("@annotation(trackParameters)")
    void track(JoinPoint joinPoint, TrackParameters trackParameters) {
        var message = createMessageString(joinPoint, trackParameters.parameterNames());
        iTrackingHandler.handleTracking(message);
    }

    private String createMessageString(JoinPoint joinPoint, String[] parameterNames) {
        var args = joinPoint.getArgs();

        return trackingAspectHelper.createMessageString(args, parameterNames);
    }
}
