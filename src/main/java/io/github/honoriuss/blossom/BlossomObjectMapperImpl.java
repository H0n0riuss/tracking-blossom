package io.github.honoriuss.blossom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class BlossomObjectMapperImpl implements ITrackingObjectMapper<String> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ITrackingParameterRegistry parameterRegistry;

    BlossomObjectMapperImpl(ITrackingParameterRegistry parameterRegistry) {
        this.parameterRegistry = parameterRegistry;
    }

    @Override
    @Deprecated
    public String mapParameters(Object[] args, String[] parameterNames) {
        var resultMap = new HashMap<String, Object>();
        var parameterNamesList = new ArrayList<>(Arrays.stream(parameterNames).toList());

        addRegistryEntries(resultMap);
        addAnnotationEntries(args, parameterNamesList, resultMap);

        return writeValueAsJsonString(resultMap);
    }

    @Override
    public String mapParameters(List<Object> args, List<String> parameterNames) {
        var resultMap = new HashMap<String, Object>();

        addRegistryEntries(resultMap);
        addAnnotationEntries(args, parameterNames, resultMap);

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

    @Deprecated
    private void addAnnotationEntries(Object[] args, List<String> parameterNamesList, HashMap<String, Object> resultMap) {
        addAnnotationEntries(Arrays.asList(args), parameterNamesList, resultMap);
    }

    private void addAnnotationEntries(List<Object> args, List<String> parameterNamesList, HashMap<String, Object> resultMap) {
        for (var i = 0; i < parameterNamesList.size(); ++i) {
            var parameterName = parameterNamesList.get(i);
            if (parameterName.isEmpty()) {
                continue;
            }
            resultMap.put(parameterName, args.get(i));
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
