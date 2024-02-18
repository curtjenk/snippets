package com.cfa.sts.integration.common.annotation.processor;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Borrowed from the SDK with small mod to return the results
 * of "task.call" instead of the assertion.
 *
 * This class assists in running an annotation processor. It does this by running the Java Compiler with the
 * specified processors and source files. This removes the need to mock all the annotation processing interfaces like
 * ProcessingEnvironment, RoundEnvironment, TypeElement, etc.
 */
@Slf4j
public class AnnotationProcessorTester {

    private final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private final StandardJavaFileManager fileManager =
            compiler.getStandardFileManager(diagnosticCollector, null, null);

    @Getter
    private final List<Processor> processors;

    private final String instanceId =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH-mm-ss-SSS").format(OffsetDateTime.now())
                    + "-" + RandomStringUtils.randomAlphanumeric(6);

    @Getter
    @Setter
    private static File defaultOutputDirectory = new File("target/annotation-processor-tester");

    @Getter
    @Setter
    private File sourceOutput =
            Paths.get(defaultOutputDirectory.getAbsolutePath(), this.instanceId, "src").toFile();

    @Getter
    @Setter
    private File classOutput =
            Paths.get(defaultOutputDirectory.getAbsolutePath(), this.instanceId, "classes").toFile();

    public AnnotationProcessorTester(Processor processor) {
        this(Collections.singletonList(processor));
    }

    static {
        if (defaultOutputDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(defaultOutputDirectory);
            } catch (IOException e) {
                log.error("Unable to delete default output directory: {}", defaultOutputDirectory.getAbsolutePath());
            }
        }
        defaultOutputDirectory.mkdirs();
    }

    @SneakyThrows
    public AnnotationProcessorTester(List<Processor> processors) {
        this.processors = processors;

        sourceOutput.mkdirs();
        classOutput.mkdirs();

        log.info("Source Output: '{}' Class Output: '{}'", sourceOutput, classOutput);

        fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Collections.singletonList(sourceOutput));
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(classOutput));
    }

    @SneakyThrows public Boolean run(String... classes) {
        var task = compiler.getTask(
                null,
                fileManager,
                diagnosticCollector,
                null,
                null,
                fileManager.getJavaFileObjects(classes));

        task.setProcessors(processors);
        try {
            var success = task.call();
            if (Boolean.FALSE.equals(success)) {
                log.error("Diagnostic errors: {}", diagnosticCollector.getDiagnostics());
            }
            return success;
        } catch (RuntimeException e) {
            if (e.getClass().equals(RuntimeException.class)) {
                throw e.getCause();
            } else {
                throw e;
            }
        }
    }
}