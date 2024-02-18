package com.cfa.sts.integration.common.factories;

import com.cfa.sts.integration.common.configuration.ExecutorServiceProperties;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class PlatformExecutorServiceFactory {

    @Getter
    protected final List<ExecutorServiceReference> registeredExecutorServices = new LinkedList<>();

    @Getter
    protected final Thread shutdownHook = new Thread(this::shutdown);

    public PlatformExecutorServiceFactory() {
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    }

    public ExecutorService build(ExecutorServiceProperties executorServiceProperties) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(executorServiceProperties.getThreadNameFormat())
                .setThreadFactory(Executors.defaultThreadFactory())
                .setDaemon(executorServiceProperties.isDaemon())
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(executorServiceProperties.getNumThreads(), threadFactory);
        synchronized (registeredExecutorServices) {
            registeredExecutorServices.add(new ExecutorServiceReference(executorService, executorServiceProperties));
        }
        return executorService;
    }

    @PreDestroy
    protected void shutdown() {
        log.info("Shutting down Integration Platform {} registered Executor Services ", registeredExecutorServices.size());
        registeredExecutorServices.parallelStream()
                .forEach((executorService -> {
                    log.info("Requesting shutdown of Integration Platform executorService");
                    executorService.getService().shutdown();
                    try {
                        if (!executorService.getService().awaitTermination(
                                executorService.getProperties().getShutdownGracePeriod().toMillis(), TimeUnit.MILLISECONDS))
                        {
                            log.warn("Forcing shutdown");
                            executorService.getService().shutdownNow();
                            if (!executorService.getService().awaitTermination(
                                    executorService.getProperties().getShutdownGracePeriod().toMillis(), TimeUnit.MILLISECONDS)) {
                                log.warn(
                                        "Executor service failed to shutdown running processes within {} seconds. "
                                                + "Those threads will now be terminated.", executorService.getProperties()
                                                .getShutdownGracePeriod());
                            }
                        }
                    } catch (InterruptedException e) {
                        log.error("Interrupt received while shutting down executor services.", e);
                        executorService.getService().shutdownNow();
                        Thread.currentThread().interrupt();
                    }
                }));
    }

    @Data
    public static class ExecutorServiceReference {

        @NonNull
        private ExecutorService service;

        @NonNull
        private ExecutorServiceProperties properties;
    }
}