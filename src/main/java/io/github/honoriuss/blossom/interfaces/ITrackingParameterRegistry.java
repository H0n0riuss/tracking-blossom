package io.github.honoriuss.blossom.interfaces;

import java.util.List;

public interface ITrackingParameterRegistry {
    void register(ITrackingParameterProvider trackingParameterProvider);
    List<ITrackingParameterProvider> getTrackingParameterProviders();
}
