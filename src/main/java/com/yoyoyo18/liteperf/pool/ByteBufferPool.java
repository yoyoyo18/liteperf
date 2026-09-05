package com.yoyoyo18.liteperf.pool;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ByteBuffer pool for short-lived direct buffers used in mesh uploads or staging.
 * This pool returns direct buffers of a fixed capacity. Not thread-safe.
 */
public final class ByteBufferPool {
    private final Deque<ByteBuffer> pool = new ArrayDeque<>();
    private final int maxSize;
    private final int capacity;

    public ByteBufferPool(int capacity, int maxSize) {
        this.capacity = capacity;
        this.maxSize = Math.max(1, maxSize);
    }

    public ByteBuffer obtain() {
        ByteBuffer b = pool.pollFirst();
        if (b == null) b = ByteBuffer.allocateDirect(capacity);
        b.clear();
        return b;
    }

    public void free(ByteBuffer b) {
        if (b == null) return;
        if (b.capacity() != capacity) return;
        if (pool.size() < maxSize) pool.offerFirst(b);
    }

    public void clear() { pool.clear(); }
}
