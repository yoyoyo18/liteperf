package com.yoyoyo18.liteperf.config;

import com.yoyoyo18.liteperf.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple config hot-reloader that polls the config file and reloads when modified.
 * Runs a single background thread. Conservative and lightweight.
 */
public final class ConfigHotReloader {
    private static final Logger LOGGER = LogManager.getLogger("liteperf:ConfigHotReloader");
    private static final AtomicBoolean running = new AtomicBoolean(false);

    public static void start() {
        if (running.getAndSet(true)) return;
        Thread t = new Thread(ConfigHotReloader::runLoop, "LitePerf-ConfigReloader");
        t.setDaemon(true);
        t.start();
    }

    private static void runLoop() {
        Path cfg = FabricLoader.getInstance().getConfigDir().resolve("liteperf.toml");
        FileTime last = null;
        while (running.get()) {
            try {
                if (cfg.toFile().exists()) {
                    FileTime ft = FileTime.fromMillis(cfg.toFile().lastModified());
                    if (last == null || ft.compareTo(last) > 0) {
                        LOGGER.info("Config change detected, reloading liteperf.toml");
                        ConfigManager.loadDefault();
                        last = ft;
                    }
                }
                Thread.sleep(2000);
            } catch (Throwable t) {
                LOGGER.error("Error in ConfigHotReloader loop", t);
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            }
        }
    }

    public static void stop() { running.set(false); }
}
