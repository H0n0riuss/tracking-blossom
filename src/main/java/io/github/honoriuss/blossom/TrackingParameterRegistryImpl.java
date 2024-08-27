package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingParameterProvider;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class TrackingParameterRegistryImpl implements ITrackingParameterRegistry {
    private final Logger logger = LoggerFactory.getLogger(TrackingParameterRegistryImpl.class);
    private final List<ITrackingParameterProvider> parameterProviderList;

    TrackingParameterRegistryImpl(List<ITrackingParameterProvider> parameterProviderList) {
        this.parameterProviderList = parameterProviderList;

        logger.info("{} parameter provider registered.", parameterProviderList.size());
    }

    @Override
    @Deprecated
    public void register(ITrackingParameterProvider trackingParameterProvider) {
        if (parameterProviderList.contains(trackingParameterProvider)) {
            logger.info("Tracking parameter provider: {} is already registered.", trackingParameterProvider.getClass().getName());
            return;
        }
        logger.info("Registering tracking parameter provider: {}", trackingParameterProvider.getClass().getName());
        parameterProviderList.add(trackingParameterProvider);
    }

    @Override
    public List<ITrackingParameterProvider> getTrackingParameterProviders() {
        return parameterProviderList;
    }
}
