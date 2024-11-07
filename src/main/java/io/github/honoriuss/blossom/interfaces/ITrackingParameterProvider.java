package io.github.honoriuss.blossom.interfaces;

import java.util.HashMap;

public interface ITrackingParameterProvider { // // TODO better name
    @Deprecated
    default HashMap<String, Object> getBaseParameters() {
        return null;
    }

    void addBaseParameters(HashMap<String, Object> parameterHashMap);
}
