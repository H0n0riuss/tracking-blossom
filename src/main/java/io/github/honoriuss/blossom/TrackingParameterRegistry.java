package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingParameterProvider;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class TrackingParameterRegistry implements ITrackingParameterRegistry {
    private final Logger logger = LoggerFactory.getLogger(TrackingParameterRegistry.class);
    private final List<ITrackingParameterProvider> baseParameterProviderList = new ArrayList<>();

    @Override
    public void register(ITrackingParameterProvider trackingParameterProvider) {
        logger.info("Registering tracking parameter provider: {}", trackingParameterProvider.getClass().getName());
        baseParameterProviderList.add(trackingParameterProvider);
    }

    @Override
    public List<ITrackingParameterProvider> getTrackingParameterProviders() {
        return baseParameterProviderList;
    }
}
