package io.github.honoriuss.blossom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class TrackingObjectMapperImpl implements ITrackingObjectMapper<String> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ITrackingParameterRegistry parameterRegistry;

    TrackingObjectMapperImpl(ITrackingParameterRegistry parameterRegistry) {
        this.parameterRegistry = parameterRegistry;
    }

    @Override
    public String mapParameters(Object[] args, String[] parameterNames) {
        var resultMap = new HashMap<String, Object>();
        var parameterNamesList = new ArrayList<>(Arrays.stream(parameterNames).toList());

        addRegistryEntries(resultMap);
        addAnnotationEntries(args, parameterNamesList, resultMap);

        return writeValueAsJsonString(resultMap);
    }

    @Override
    public String mapResult(Object result) {
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRegistryEntries(HashMap<String, Object> resultMap) {
        for (var provider : parameterRegistry.getTrackingParameterProviders()) {
            var baseParameters = provider.getBaseParameters();
            if (baseParameters == null) {
                continue;
            }
            resultMap.putAll(baseParameters);
        }
    }

    private void addAnnotationEntries(Object[] args, ArrayList<String> parameterNamesList, HashMap<String, Object> resultMap) {
        for (var i = 0; i < parameterNamesList.size(); ++i) {
            var parameterName = parameterNamesList.get(i);
            if (parameterName.isEmpty()) {
                continue;
            }
            resultMap.put(parameterName, args[i]);
        }
    }

    private String writeValueAsJsonString(HashMap<String, Object> resultMap) {
        try {
            return mapper.writeValueAsString(resultMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
