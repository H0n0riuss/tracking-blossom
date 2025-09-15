package io.github.honoriuss.blossom.interfaces;

public interface ITrackingWriter<T> {
    default void write(T message) {
    }
}
