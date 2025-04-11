package io.github.honoriuss.blossom.interfaces;

import io.github.honoriuss.blossom.annotations.AppContext;

import java.util.List;

public interface ITrackingAppContextHandler {
    void addAppContext(List<Object> args, List<String> parameterNames, AppContext appContext);
}
