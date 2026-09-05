package com.yoyoyo18.liteperf;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class ModCompat {
    private static final Logger LOGGER = LogManager.getLogger("liteperf:ModCompat");

    public static boolean SODIUM = false;
    public static boolean LITHIUM = false;
    public static boolean FERRITECORE = false;

    public static void detect() {
        SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
        LITHIUM = FabricLoader.getInstance().isModLoaded("lithium");
        FERRITECORE = FabricLoader.getInstance().isModLoaded("ferritecore");

        LOGGER.info("Mod compatibility detection: Sodium={}, Lithium={}, FerriteCore={}", SODIUM, LITHIUM, FERRITECORE);

        // Log versions where possible
        logVersion("sodium");
        logVersion("lithium");
        logVersion("ferritecore");
    }

    private static void logVersion(String id) {
        Optional<ModContainer> c = FabricLoader.getInstance().getModContainer(id);
        if (c.isPresent()) {
            ModMetadata m = c.get().getMetadata();
            LOGGER.info("{} version={}", id, m.getVersion().getFriendlyString());
        }
    }
}
