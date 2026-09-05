package com.yoyoyo18.liteperf.render;

import com.yoyoyo18.liteperf.ConfigManager;
import com.yoyoyo18.liteperf.ModCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Culling adapter: conservative, compatibility-first frustum/distance culling.
 * When Sodium is present, LitePerf will avoid risky renderer-side mesh changes and use
 * conservative checks here. We do not call Sodium internals to avoid hard dependencies.
 */
public final class CullingAdapter {
    private static final Logger LOGGER = LogManager.getLogger("liteperf:CullingAdapter");

    private CullingAdapter() {}

    public static boolean isEntityVisible(Entity entity) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc == null || mc.player == null || mc.world == null) return true; // be safe: assume visible

            // respect config throttle distance
            double throttleDistance = ConfigManager.getDouble("throttleDistance", 64.0);
            Vec3d camPos = mc.gameRenderer.getCamera().getPos();
            double dx = entity.getX() - camPos.x;
            double dy = entity.getY() - camPos.y;
            double dz = entity.getZ() - camPos.z;
            double distSq = dx*dx + dy*dy + dz*dz;
            if (distSq > throttleDistance * throttleDistance) return false;

            // If Sodium present, be conservative: only distance cull, don't attempt per-pixel occlusion checks.
            if (ModCompat.SODIUM) return true; // Let Sodium handle advanced occlusion; don't aggressively cull here.

            // Basic angle-based culling: check if entity is roughly in front of player.
            ClientPlayerEntity player = mc.player;
            Vec3d look = getLookVector(player);
            Vec3d toEnt = new Vec3d(dx, dy, dz);
            double dot = look.x * toEnt.x + look.y * toEnt.y + look.z * toEnt.z;
            // If dot <= 0, the entity is behind or perpendicular; in that case cull it (conservative threshold)
            return dot > 0 || distSq < 16.0; // always show very close entities
        } catch (Throwable t) {
            LOGGER.debug("CullingAdapter failure", t);
            return true; // on error, assume visible
        }
    }

    private static Vec3d getLookVector(ClientPlayerEntity player) {
        // use player's yaw/pitch to compute a forward vector
        double yaw = Math.toRadians(player.getYaw());
        double pitch = Math.toRadians(player.getPitch());
        double x = -Math.sin(yaw) * Math.cos(pitch);
        double y = -Math.sin(pitch);
        double z = Math.cos(yaw) * Math.cos(pitch);
        return new Vec3d(x, y, z);
    }
}
