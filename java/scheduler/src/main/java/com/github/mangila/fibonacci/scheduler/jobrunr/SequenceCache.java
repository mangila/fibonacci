package com.github.mangila.fibonacci.scheduler.jobrunr;

import com.github.mangila.fibonacci.scheduler.properties.ComputeProperties;
import org.springframework.stereotype.Component;

import java.util.BitSet;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SequenceCache {

    private final BitSet cache;
    private final BitSet inFlight;
    private final ReentrantLock lock = new ReentrantLock();

    public SequenceCache(ComputeProperties computeProperties) {
        this.cache = new BitSet(computeProperties.getMax() + 1);
        this.inFlight = new BitSet(computeProperties.getMax() + 1);
    }

    void put(int sequence) {
        lock.lock();
        try {
            cache.set(sequence, true);
        } finally {
            lock.unlock();
        }
    }

    boolean tryCompute(int sequence) {
        lock.lock();
        try {
            if (cache.get(sequence) || inFlight.get(sequence)) {
                return false;
            }
            inFlight.set(sequence, true);
            return true;
        } finally {
            lock.unlock();
        }
    }

    void release(int sequence) {
        lock.lock();
        try {
            inFlight.set(sequence, false);
        } finally {
            lock.unlock();
        }
    }
}
