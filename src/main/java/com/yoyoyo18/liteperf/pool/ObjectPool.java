package com.yoyoyo18.liteperf.pool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * Very small generic object pool for short-lived objects used in tight loops.
 * Not thread-safe by design; callers should only use from a single thread (client thread)
 * or add external synchronization when used from worker threads.
 */
public class ObjectPool<T> {
    private final Supplier<T> supplier;
    private final Deque<T> pool = new ArrayDeque<>();
    private final int maxSize;

    public ObjectPool(Supplier<T> supplier, int maxSize) {
        this.supplier = supplier;
        this.maxSize = Math.max(1, maxSize);
    }

    public T obtain() {
        T obj = pool.pollFirst();
        return obj != null ? obj : supplier.get();
    }

    public void free(T obj) {
        if (pool.size() < maxSize) pool.offerFirst(obj);
    }

    public void clear() {
        pool.clear();
    }
}
