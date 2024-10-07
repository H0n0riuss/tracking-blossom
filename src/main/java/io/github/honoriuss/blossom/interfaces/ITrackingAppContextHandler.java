package io.github.honoriuss.blossom.interfaces;

import io.github.honoriuss.blossom.annotations.AppContext;

import java.util.ArrayList;

public interface ITrackingAppContextHandler {
    void addAppContext(ArrayList<Object> args, ArrayList<String> parameterNames, AppContext appContext);
}
