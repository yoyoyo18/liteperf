package com.yoyoyo18.liteperf.async;

import com.yoyoyo18.liteperf.LitePerfMod;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Very conservative async mesh worker used to offload heavy tasks (mesh building) from the client thread.
 * This is a compatibility-first skeleton: it will not interact with Minecraft's internal chunk upload paths
 * directly — instead it exposes a queue and executes runnables. Integration points with the game's
 * mesh upload should be implemented carefully and guarded by compatibility checks.
 */
public final class AsyncMeshWorker {
    private static final int WORKER_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    private static final ExecutorService executor = Executors.newFixedThreadPool(WORKER_COUNT, r -> {
        Thread t = new Thread(r, "LitePerf-MeshWorker");
        t.setDaemon(true);
        return t;
    });

    public static void submit(Runnable r) {
        try {
            executor.submit(() -> {
                try {
                    r.run();
                } catch (Throwable t) {
                    LitePerfMod.LOGGER.error("Error in async mesh worker task", t);
                }
            });
        } catch (Throwable t) {
            LitePerfMod.LOGGER.warn("Async mesh worker submission failed, running on caller thread", t);
            r.run();
        }
    }

    public static void shutdown() {
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }
}
