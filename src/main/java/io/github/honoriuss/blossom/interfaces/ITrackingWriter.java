package io.github.honoriuss.blossom.interfaces;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITrackingWriter<T> {
    default void write(T message) {
    }

    default Mono<T> writeMono(T message) {
        return Mono.empty();
    }

    default Flux<T> writeFlux(T message) {
        return Flux.empty();
    }
}
