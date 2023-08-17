package com.cfa.sts.integration.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
@Slf4j
public class JsonUtil {

    private JsonUtil() {}

    public static Optional<String> toEscapedJson(@NonNull ObjectMapper mapper, @NonNull Object o) {
        try {
            var jsonString = mapper.writeValueAsString(o);
            return Optional.of(new String(JsonStringEncoder.getInstance().quoteAsString(jsonString)));
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
            return Optional.empty();
        }
    }
}