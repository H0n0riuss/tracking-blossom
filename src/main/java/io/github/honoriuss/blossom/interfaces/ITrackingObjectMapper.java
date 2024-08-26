package io.github.honoriuss.blossom.interfaces;

import java.util.List;

public interface ITrackingObjectMapper<T> {
    @Deprecated
    T mapParameters(Object[] args, String[] parameterNames);
    T mapParameters(List<Object> args, List<String> parameterNames);
    T mapResult(Object obj);
}
