package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class BlossomWriterImpl<T> implements ITrackingWriter<T> {
    private final Logger logger = LoggerFactory.getLogger(BlossomWriterImpl.class);

    @Override
    public void write(T message) {
        logger.info("write: {}", message);
    }

    @Override
    public Mono<T> writeMono(T message) {
        return Mono.just(message);
    }

    @Override
    public Flux<T> writeFlux(T message) {
        return Flux.just(message);
    }
}
