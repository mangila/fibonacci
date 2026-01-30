package com.github.mangila.fibonacci.postgres;

public record FibonacciMetadataProjection(int id,
                                          boolean sentToZset,
                                          boolean sentToStream) {
}
