package io.github.honoriuss.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.honoriuss.tracking.interfaces.ITrackingObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class TrackingObjectMapperImpl implements ITrackingObjectMapper<String> {
    private final Logger logger = LoggerFactory.getLogger(TrackingObjectMapperImpl.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final TrackingProperties trackingProperties;

    TrackingObjectMapperImpl(TrackingProperties trackingProperties) {
        this.trackingProperties = trackingProperties;
        logger.info("using default col name: {}", trackingProperties.getColumnName());
    }

    @Override
    public String mapParameters(Object[] args, String[] parameterNames) {
        var resultMap = new HashMap<String, Object>();
        var normalisedParameterNames = getNormaliseParameterNames(args.length, parameterNames);

        for (var i = 0; i < args.length; ++i) {
            resultMap.put(normalisedParameterNames.get(i), args[i]);
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

    private List<String> getNormaliseParameterNames(int argSize, String[] parameterNames) {
        var resList = new ArrayList<>(Arrays.stream(parameterNames).toList());
        if (resList.size() < argSize) {
            logger.info("Found more args: argSize: {}, parameterNames.size(): {}", argSize, parameterNames.length);
            resList.add(trackingProperties.getColumnName());
            var diff = resList.size() - argSize;
            for (var i = 0; i < diff; ++i) {
                resList.add(trackingProperties.getColumnName() + i);
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
