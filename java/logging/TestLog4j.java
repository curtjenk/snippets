package com.cfa.api.stsintegrationplatform.experience.api.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cfa.api.stsintegrationplatform.experience.api.exception.InvalidDateRangeException;
import com.cfa.api.stsintegrationplatform.experience.api.exception.InvalidPageSizeException;
import com.cfa.api.stsintegrationplatform.experience.api.mapper.MetricMapper;
import com.cfa.api.stsintegrationplatform.experience.api.mapper.PayloadMapper;
import com.cfa.api.stsintegrationplatform.experience.api.util.MockEventObjects;
import com.cfa.api.stsintegrationplatform.experience.api.util.MockExperienceObjects;
import com.cfa.sts.integration.common.repository.MetricRepository;
import com.cfa.sts.integration.common.repository.PayloadRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationEventServiceTest {

    @Mock
    private PayloadMapper payloadMapper;
    @Mock
    private PayloadRepository payloadRepository;
    @Mock
    MetricMapper metricMapper;
    @Mock
    MetricRepository metricRepository;

    @InjectMocks
    private OperationEventService operationEventService;

    private AutoCloseable closeable;

    private final String correlationId = "9d2dee33-7803-485a-a2b1-2c7538e597ee";
    private final String startDate = "2021-06-01T12:15:41Z";
    private OffsetDateTime offsetStartDate;
    private final String endDate = "2023-07-01T12:55:41Z";
    private OffsetDateTime offsetEndDate;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        operationEventService = new OperationEventService(payloadMapper, payloadRepository, metricMapper, metricRepository);

        final var log = (Logger) LoggerFactory.getLogger(OperationEventService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);

        offsetStartDate = ZonedDateTime.parse(startDate).toOffsetDateTime();
        offsetEndDate = ZonedDateTime.parse(endDate).toOffsetDateTime();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        listAppender.stop();
    }

    @Test
    @DisplayName("Duplicate metric ignored")
    void ignore_dupe_metric() {
        when(metricRepository.saveAndFlush(any())).thenThrow(DataIntegrityViolationException.class);
        // call the method that should write to the logger
        operationEventService.processOperationMetricEvent(MockEventObjects.getMetricEventData());
        String logMessage = listAppender.list.get(0).getFormattedMessage();
        assertTrue(logMessage.contains("Ignoring duplicate/invalid Metric"));
    }
    @Test
    @DisplayName("Duplicate payload ignored")
    void ignore_dupe_payload() {
        when(payloadRepository.saveAndFlush(any())).thenThrow(DataIntegrityViolationException.class);
        operationEventService.processOperationPayloadEvent(MockEventObjects.getPayloadEventData());
        String logMessage = listAppender.list.get(0).getFormattedMessage();
        assertTrue(logMessage.contains("Ignoring duplicate/invalid Payload"));
    }


}