package com.cfa.sts.integration.common.annotation.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InteractionAnnotationProcessorTest {

    private com.cfa.sts.integration.common.annotation.processor.InteractionAnnotationProcessor annotationProcessor;

    private AnnotationProcessorTester annotationProcessorTester;

    @BeforeEach
    public void setup() {
        this.annotationProcessor = new InteractionAnnotationProcessor();
        annotationProcessorTester = new AnnotationProcessorTester(this.annotationProcessor);
    }

    @Test
    void happy_path() {
        var success = annotationProcessorTester.run("src/test/resources/annotation/interaction/InteractionHappyPath.java");
        assertTrue(success);
    }
    @Test
    void shouldFail_duplicate_context_single_file() {
        var success = annotationProcessorTester.run(
                "src/test/resources/annotation/interaction/InteractionContextDuplicated.java");
        assertFalse(success);
    }
    @Test
    void shouldFail_duplicate_context_multi_file() {
        var success = annotationProcessorTester.run(
                "src/test/resources/annotation/interaction/InteractionContextDuplicated1.java",
                "src/test/resources/annotation/interaction/InteractionContextDuplicated2.java");
        assertFalse(success);
    }
    @Test
    void shouldFail_context_invalid() {
        var success = annotationProcessorTester.run(
                "src/test/resources/annotation/interaction/InteractionContextInvalid.java");
        assertFalse(success);
    }
    @Test
    void direction_in_transport_mismatch() {
        var success =  annotationProcessorTester.run(
                "src/test/resources/annotation/interaction/InteractionInDirectionTransportMismatch.java");
        assertFalse(success);
    }
    @Test
    void direction_out_transport_mismatch() {
        var success =  annotationProcessorTester.run(
                "src/test/resources/annotation/interaction/InteractionOutDirectionTransportMismatch.java");
        assertFalse(success);
    }
    @Test
    void direction_internal_transport_mismatch() {
        var success =  annotationProcessorTester.run(
                "src/test/resources/annotation/interaction/InteractionInternalDirectionTransportMismatch.java");
        assertFalse(success);
    }
    @Test
    void direction_internal_function_mismatch() {
        var success =  annotationProcessorTester.run(
                "src/test/resources/annotation/interaction/InteractionInternalDirectionFunctionMismatch.java");
        assertFalse(success);
    }
    @Test
    void direction_in_function_mismatch() {
        var success =  annotationProcessorTester.run(
                "src/test/resources/annotation/interaction/InteractionInDirectionFunctionMismatch.java");
        assertTrue(success);
    }
}