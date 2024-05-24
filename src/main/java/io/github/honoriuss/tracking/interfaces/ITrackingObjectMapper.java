package io.github.honoriuss.tracking.interfaces;

public interface ITrackingObjectMapper<T> {
    T mapParameters(Object[] args, String[] parameterNames);

    T mapResult(Object obj);
}
