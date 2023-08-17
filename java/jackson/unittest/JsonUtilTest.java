package com.cfa.sts.integration.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class JsonUtilTest {

    @Mock
    private ObjectMapper mockObjectMapper;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    @Test
    @DisplayName("Throws NullPointerException with null object input")
    void handles_nullObject() {
        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertThrows(NullPointerException.class, () -> JsonUtil.toEscapedJson(mapper,null));
    }

    @Test
    @DisplayName("Throws NullPointerException with null object mapper")
    void handles_nullMapper() {
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("key1", "value1");
        testMap.put("key2", "value2");
        testMap.put("key3", 123);
        Assertions.assertThrows(NullPointerException.class, () -> JsonUtil.toEscapedJson(null,testMap));
    }

    @Test
    @DisplayName("Handles JsonProcessingException")
    void handles_mapperJsonProcessingException() throws JsonProcessingException {
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("key1", "value1");
        var exception = new MockJsonProcessingException("Because of protected constructors");
        when(mockObjectMapper.writeValueAsString(any())).thenThrow(exception);
        var escapedJsonString = JsonUtil.toEscapedJson(mockObjectMapper, testMap);
        verify(mockObjectMapper, times(1)).writeValueAsString(any());
        Assertions.assertFalse(escapedJsonString.isPresent());
    }
    @Test
    @DisplayName("Converts object to escaped json string")
    void converts() {
        ObjectMapper mapper = new ObjectMapper();
        var expected = "{\\\"key1\\\":\\\"value1\\\",\\\"key2\\\":\\\"value2\\\",\\\"key3\\\":123}";
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("key1", "value1");
        testMap.put("key2", "value2");
        testMap.put("key3", 123);
        var escapedJsonString = JsonUtil.toEscapedJson(mapper, testMap);
        Assertions.assertTrue(escapedJsonString.isPresent());
        Assertions.assertEquals(expected, escapedJsonString.get());
    }

    private static class MockJsonProcessingException extends JsonProcessingException {
        public MockJsonProcessingException(String message) {
            super(message);
        }
    }
}