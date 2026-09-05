package com.yoyoyo18.liteperf.pool;

import net.minecraft.util.math.Vec3d;

/**
 * Simple pool for Vec3d objects to avoid frequent allocations in hot paths.
 */
public class Vec3dPool {
    private static final ObjectPool<Vec3d> pool = new ObjectPool<>(() -> new Vec3d(0,0,0), 256);

    public static Vec3d obtain() { return pool.obtain(); }
    public static void free(Vec3d v) { pool.free(v); }
}
