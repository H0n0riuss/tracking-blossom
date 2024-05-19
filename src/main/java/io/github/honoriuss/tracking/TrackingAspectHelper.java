package io.github.honoriuss.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class TrackingAspectHelper {
    private final Logger logger = LoggerFactory.getLogger(TrackingAspectHelper.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String standardColName;

    TrackingAspectHelper(String standardColName) {
        this.standardColName = standardColName;
    }

    public String createMessageString(Object[] args, String[] parameterNames) {
        var resultMap = new HashMap<String, Object>();
        var normalisedParameterNames = getNormaliseParameterNames(args.length, parameterNames);

        for (var i = 0; i < args.length; ++i) {
            resultMap.put(normalisedParameterNames.get(i), args[i]);
        }
        return writeValueAsJsonString(resultMap);
    }

    public String createMessageString(Object result) {
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getNormaliseParameterNames(int argSize, String[] parameterNames) {
        var resList = new ArrayList<>(Arrays.stream(parameterNames).toList());
        if (resList.size() < argSize) {
            logger.info("Found more args: argSize: {}, parameterNames.size(): {}", argSize, parameterNames.length);
            resList.add(standardColName);
            var diff = resList.size() - argSize;
            for (var i = 0; i < diff; ++i) {
                resList.add(standardColName + i);
            }
        }
        return resList;
    }

    private String writeValueAsJsonString(HashMap<String, Object> resultMap) {
        try {
            return mapper.writeValueAsString(resultMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
