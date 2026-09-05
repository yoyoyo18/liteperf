package com.yoyoyo18.liteperf;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LitePerfMod implements ClientModInitializer {
    public static final String MODID = "liteperf";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("LitePerf initializing");
        ConfigManager.loadDefault();

        // Example: register a client tick listener to drive throttling / scheduling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                ThrottleManager.onClientTick(client);
            } catch (Throwable t) {
                LOGGER.error("Error in LitePerf tick handler", t);
            }
        });
    }
}
