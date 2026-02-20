package com.github.mangila.fibonacci.postgres;

public record FibonacciMetadataProjection(int id,
                                          boolean scheduled,
                                          boolean computed,
                                          String algorithm) {

    public static FibonacciMetadataProjection newInsert(int id, String algorithm) {
        return new FibonacciMetadataProjection(id, false, false, algorithm);
    }

    public static FibonacciMetadataProjection scheduled(int id, String algorithm) {
        return new FibonacciMetadataProjection(id, true, false, algorithm);
    }

    public static FibonacciMetadataProjection computed(int id, String algorithm) {
        return new FibonacciMetadataProjection(id, true, true, algorithm);
    }

}
