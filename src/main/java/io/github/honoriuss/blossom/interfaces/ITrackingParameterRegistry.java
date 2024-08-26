package io.github.honoriuss.blossom.interfaces;

import java.util.List;

public interface ITrackingParameterRegistry {
    default void register(ITrackingParameterProvider trackingParameterProvider) {
    }

    List<ITrackingParameterProvider> getTrackingParameterProviders();
}
