package com.yoyoyo18.liteperf;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LitePerfMod implements ClientModInitializer {
    public static final String MODID = "liteperf";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("LitePerf initializing");
        // Load config (tries config/liteperf.toml first, else packaged default)
        ConfigManager.loadDefault();

        // Detect common mods and set compatibility behavior
        ModCompat.detect();

        // Adjust default feature flags when incompatible mods are detected
        applyCompatibilitySafeguards();

        // Register client tick listener to drive throttling / scheduling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                ThrottleManager.onClientTick(client);
            } catch (Throwable t) {
                LOGGER.error("Error in LitePerf tick handler", t);
            }
        });

        LOGGER.info("LitePerf initialized. Minecraft: {} | Loader: {} | ConfigFrom: {}",
                FabricLoader.getInstance().getModContainer("minecraft").map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("unknown"),
                FabricLoader.getInstance().getModContainer("fabricloader").map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("unknown"),
                ConfigManager.getString("_source", "embedded")
        );
    }

    private void applyCompatibilitySafeguards() {
        boolean preferCompat = ConfigManager.getBoolean("compat.preferCompatibility", true);

        if (ModCompat.SODIUM && preferCompat) {
            LOGGER.info("Sodium detected — disabling features that conflict with Sodium by default (asyncChunkMeshUpload, instancing). Set compat.preferCompatibility=false to override");
            ConfigManager.overrideBoolean("asyncChunkMeshUpload", false);
            ConfigManager.overrideBoolean("instancing", false);
        }

        if ((ModCompat.LITHIUM || ModCompat.FERRITECORE) && preferCompat) {
            LOGGER.info("Lithium/Ferrite detected — avoiding server-side tick changes and deferring to server-side optimizers.");
            ConfigManager.overrideBoolean("entityTickThrottling", false);
            ConfigManager.overrideBoolean("blockEntityTickThrottling", false);
        }

        // Allow user to explicitly force-enable experimental render features even when mods are present
        if (ConfigManager.getBoolean("render.frameGeneration", false)) {
            LOGGER.warn("Frame generation is experimental — ensure you understand it may cause artifacts or be incompatible with shader mods.");
        }
    }
}
