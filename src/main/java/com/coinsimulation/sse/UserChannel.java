package com.coinsimulation.sse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Slf4j
public class UserChannel {
    private EmitterProcessor<String> processor;
    private Flux<String> flux;
    private FluxSink<String> sink;
    private Runnable closeCallback;

    public UserChannel() {
        processor = EmitterProcessor.create();
        this.sink = processor.sink();
        this.flux = processor
                .doOnCancel(() -> {
                    log.info("doOnCancel, downstream " + processor.downstreamCount());
                    if (processor.downstreamCount() == 1) close();
                })
                .doOnTerminate(() -> {
                    log.info("doOnTerminate, downstream " + processor.downstreamCount());
                });
    }

    public void send(String message) {
        sink.next(message);
    }

    public Flux<String> toFlux() {
        return flux;
    }

    private void close() {
        if (closeCallback != null) closeCallback.run();
        sink.complete();
    }

    public UserChannel onClose(Runnable closeCallback) {
        this.closeCallback = closeCallback;
        return this;
    }
}