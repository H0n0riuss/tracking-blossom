package io.github.honoriuss.blossom.interfaces;

import io.github.honoriuss.blossom.annotations.AdditionalTrackingInfo;

public interface ITrackingHandler<T> {
    default void handleTracking(T message) {
    }

    default void handleTracking(T message, AdditionalTrackingInfo additionalTrackingInfo) {
    }
}
