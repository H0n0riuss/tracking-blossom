package io.github.honoriuss.tracking.interfaces;

public interface ITrackingHandler<T> {
    void handleTracking(T message);
}
