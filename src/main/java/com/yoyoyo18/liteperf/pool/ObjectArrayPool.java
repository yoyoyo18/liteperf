package com.yoyoyo18.liteperf.pool;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Simple pool for reusing Object[] arrays to avoid frequent allocations.
 * Not thread-safe; callers should synchronize if used across threads.
 */
public final class ObjectArrayPool {
    private static final int DEFAULT_MAX = 128;
    private final Deque<Object[]> pool = new ArrayDeque<>();
    private final int maxSize;
    private final int arraySize;

    public ObjectArrayPool(int arraySize) {
        this(arraySize, DEFAULT_MAX);
    }

    public ObjectArrayPool(int arraySize, int maxSize) {
        this.arraySize = arraySize;
        this.maxSize = Math.max(1, maxSize);
    }

    public Object[] obtain() {
        Object[] a = pool.pollFirst();
        if (a == null) a = new Object[arraySize];
        return a;
    }

    public void free(Object[] a) {
        if (a == null) return;
        if (a.length != arraySize) return;
        if (pool.size() < maxSize) {
            // clear references to avoid leaks
            for (int i = 0; i < a.length; i++) a[i] = null;
            pool.offerFirst(a);
        }
    }

    public void clear() { pool.clear(); }
}
