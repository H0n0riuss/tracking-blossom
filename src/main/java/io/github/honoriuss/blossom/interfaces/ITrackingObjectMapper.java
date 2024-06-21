package io.github.honoriuss.blossom.interfaces;

public interface ITrackingObjectMapper<T> {
    T mapParameters(Object[] args, String[] parameterNames);

    T mapResult(Object obj);
}
