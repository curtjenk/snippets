package com.cfa.taxauditlib.stslocationapi.client.service;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.cfa.taxauditlib.data.model.RTLocationDto;
import com.cfa.taxauditlib.stslocationapi.client.service.StsLocationApiRestClient;
import com.cfa.taxauditlib.stslocationapi.client.configuration.StsLocationApiProperties;
import com.cfa.taxauditlib.stslocationapi.client.dto.LocationApiResponse;
import com.cfa.taxauditlib.stslocationapi.client.dto.LocationTaxCalcApiResponse;
import com.cfa.taxauditlib.stslocationapi.client.mapper.StsLocationApiRespRTLocationDtoMapper;
import com.cfa.taxauditlib.stslocationapi.client.mapper.StsLocationApiTaxCalcMethodRespMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestWithResources
public class StsLocationApiRestClientTest {

    private StsLocationApiRestClient stsLocationApiRestClient;
    @Mock
    private StsLocationApiProperties stsLocationApiProperties;
    @Mock
    private RestTemplate restTemplate;

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @GivenJsonResource("/data/stslocationapi-all-locations.json")
    LocationApiResponse expectedLocApiResp;
    @GivenJsonResource("/data/stslocationapi-all-locations-taxcalcmethod.json")
    LocationTaxCalcApiResponse expectedLocTaxCalcApiResp;

    @BeforeEach
    void setup() {
        var stsLocationApiRespRTLocationDtoMapper = new StsLocationApiRespRTLocationDtoMapper();
        var stsLocationApiTaxCalcMethodRespMapper = new StsLocationApiTaxCalcMethodRespMapper();
        stsLocationApiRestClient = new StsLocationApiRestClient(stsLocationApiProperties,
                    stsLocationApiRespRTLocationDtoMapper,
                    stsLocationApiTaxCalcMethodRespMapper,
                    restTemplate
                );
    }

    @Test
    @DisplayName("Get locations without tax calc method and map")
    void should_getLocationsOnly_mapResults() throws Exception {
        when(stsLocationApiProperties.getOktaUrl()).thenReturn("");
        when(stsLocationApiProperties.getBaseUrl()).thenReturn("");
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"access_token\": \"token\"}", HttpStatus.OK));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(LocationApiResponse.class)))
                .thenReturn(new ResponseEntity<>(expectedLocApiResp, HttpStatus.OK));

        verify(restTemplate, times(0)).exchange(anyString(),
                        eq(HttpMethod.GET),
                        any(),
                        eq(LocationTaxCalcApiResponse.class));

        List<RTLocationDto> locOnlyDtos =  stsLocationApiRestClient.getLocations();
        assertEquals(expectedLocApiResp.getLocationList().size(), locOnlyDtos.size());
        var loc04200 = locOnlyDtos.stream().filter(dto -> dto.getLocationNumber().equals("04200")).findFirst();
        assertTrue(loc04200.isPresent());
        assertNull(loc04200.get().getLocTaxCalcMethod());
    }

    @Test
    @DisplayName("Get locations and tax calc method data and map")
    void should_getLocationsWTaxCalcMethod_mapResults() throws Exception {
        when(stsLocationApiProperties.getOktaUrl()).thenReturn("");
        when(stsLocationApiProperties.getBaseUrl()).thenReturn("");
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"access_token\": \"token\"}", HttpStatus.OK));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(LocationApiResponse.class)))
                .thenReturn(new ResponseEntity<>(expectedLocApiResp, HttpStatus.OK));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(LocationTaxCalcApiResponse.class)))
                .thenReturn(new ResponseEntity<>(expectedLocTaxCalcApiResp, HttpStatus.OK));

        List<RTLocationDto> rtLocationDtos =  stsLocationApiRestClient.getAllLocations();
        assertEquals(expectedLocApiResp.getLocationList().size(), rtLocationDtos.size());
        var loc01783 = rtLocationDtos.stream().filter(dto -> dto.getLocationNumber().equals("01783")).findFirst();
        assertTrue(loc01783.isPresent());
        assertEquals("Egg Harbor FSU", loc01783.get().getLocationName());
        assertEquals("Local Tax Calculation", loc01783.get().getLocTaxCalcMethod());


    }
}
