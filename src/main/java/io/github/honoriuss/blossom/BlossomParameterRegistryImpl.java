package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingParameterProvider;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class BlossomParameterRegistryImpl implements ITrackingParameterRegistry {
    private final Logger logger = LoggerFactory.getLogger(BlossomParameterRegistryImpl.class);
    private final List<ITrackingParameterProvider> parameterProviderList;

    BlossomParameterRegistryImpl(List<ITrackingParameterProvider> parameterProviderList) {
        this.parameterProviderList = parameterProviderList;

        logger.debug("{} parameter provider registered.", parameterProviderList.size());
    }

    @Override
    @Deprecated
    public void register(ITrackingParameterProvider trackingParameterProvider) {
        if (parameterProviderList.contains(trackingParameterProvider)) {
            logger.debug("Tracking parameter provider: {} is already registered.", trackingParameterProvider.getClass().getName());
            return;
        }
        logger.debug("Registering tracking parameter provider: {}", trackingParameterProvider.getClass().getName());
        parameterProviderList.add(trackingParameterProvider);
    }

    @Override
    public List<ITrackingParameterProvider> getTrackingParameterProviders() {
        return parameterProviderList;
    }
}
