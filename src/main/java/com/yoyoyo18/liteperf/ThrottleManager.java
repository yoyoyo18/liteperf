package com.yoyoyo18.liteperf;

import com.yoyoyo18.liteperf.async.AsyncMeshWorker;
import com.yoyoyo18.liteperf.render.CullingAdapter;
import com.yoyoyo18.liteperf.input.Keybinds;
import com.yoyoyo18.liteperf.throttle.NoThrottle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Environment(EnvType.CLIENT)
public class ThrottleManager {
    private static final Logger LOGGER = LogManager.getLogger("liteperf:ThrottleManager");

    // Very small list of whitelisted classes that should never be throttled (populated by mods)
    private static final List<Class<?>> whitelist = new CopyOnWriteArrayList<>();

    public static void registerWhitelist(Class<?> clazz) { whitelist.add(clazz); }

    public static void onClientTick(MinecraftClient client) {
        try {
            // Poll keybinds
            Keybinds.poll();

            if (!ConfigManager.getBoolean("enabled", true)) return;

            // Submit housekeeping tasks to async worker if enabled
            if (ConfigManager.getBoolean("asyncChunkMeshUpload", true)) {
                AsyncMeshWorker.submit(() -> {
                    // Placeholder: mesh building tasks would go here (compat-aware)
                });
            }

            // Throttle client-side entity render updates conservatively
            if (client.world != null && client.player != null && ConfigManager.getBoolean("entityTickThrottling", true)) {
                double throttleDistance = ConfigManager.getDouble("throttleDistance", 64.0);
                // Iterate all entities in world; conservative approach: use vanilla entity list
                for (Entity e : client.world.getEntities()) {
                    if (e == null) continue;
                    Class<?> cls = e.getClass();
                    if (cls.isAnnotationPresent(NoThrottle.class)) continue;
                    if (whitelist.contains(cls)) continue;

                    // If not visible according to culling adapter, we can skip client-side update calls
                    boolean vis = CullingAdapter.isEntityVisible(e);
                    if (!vis) {
                        // Skip non-essential entity client-side updates. We only modify visual/client-only methods.
                        try {
                            // Call a safe hook if the entity implements a LitePerf-friendly interface
                            // (no-op here) — in the future, we can call a defined accessor to disable render ticks.
                        } catch (Throwable ignore) {}
                    }
                }
            }

        } catch (Throwable t) {
            LOGGER.error("Error in ThrottleManager", t);
        }
    }

    public static void addWhitelist(Class<?> clazz) { registerWhitelist(clazz); }
}
