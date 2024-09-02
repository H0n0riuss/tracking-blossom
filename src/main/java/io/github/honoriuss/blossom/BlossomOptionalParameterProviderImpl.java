package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingParameterProvider;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

class BlossomOptionalParameterProviderImpl implements ITrackingParameterProvider {
    private final Map<String, String> headers;
    private final HttpServletRequest request;

    public BlossomOptionalParameterProviderImpl(Map<String, String> headers, HttpServletRequest request) {
        this.headers = headers;
        this.request = request;
    }

    @Override
    public HashMap<String, Object> getBaseParameters() {
        var map = new HashMap<String, Object>();
        for (var headerKey : headers.keySet()) {
            map.put(headers.get(headerKey), request.getHeader(headerKey));
        }
        return map;
    }
}
