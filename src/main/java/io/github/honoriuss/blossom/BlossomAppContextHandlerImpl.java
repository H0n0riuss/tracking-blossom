package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.annotations.AppContext;
import io.github.honoriuss.blossom.interfaces.ITrackingAppContextHandler;

import java.util.ArrayList;

class BlossomAppContextHandlerImpl implements ITrackingAppContextHandler {

    @Override
    public void addAppContext(ArrayList<Object> args, ArrayList<String> parameterNames, AppContext appContext) {
        if (appContext == null) {
            return;
        }
        args.add(appContext.app());
        parameterNames.add(appContext.appKey());
    }
}
