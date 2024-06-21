package io.github.honoriuss.blossom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class TrackingObjectMapperImpl implements ITrackingObjectMapper<String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String mapParameters(Object[] args, String[] parameterNames) {
        var resultMap = new HashMap<String, Object>();
        var parameterNamesList = new ArrayList<>(Arrays.stream(parameterNames).toList());

        for (var i = 0; i < parameterNamesList.size(); ++i) {
            var parameterName = parameterNamesList.get(i);
            if (parameterName.isEmpty()) {
                continue;
            }
            resultMap.put(parameterName, args[i]);
        }
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

    private String writeValueAsJsonString(HashMap<String, Object> resultMap) {
        try {
            return mapper.writeValueAsString(resultMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
