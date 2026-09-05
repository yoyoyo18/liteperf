package com.yoyoyo18.liteperf.input;

import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

/**
 * Registers a simple keybinding to toggle LitePerf debug/profiling mode.
 */
public final class Keybinds {
    private static KeyBinding toggleDebugKey;

    public static void register() {
        toggleDebugKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.liteperf.toggle_debug",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "category.liteperf"
        ));
    }

    public static void poll() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        if (toggleDebugKey != null && toggleDebugKey.wasPressed()) {
            boolean prev = "true".equalsIgnoreCase(com.yoyoyo18.liteperf.ConfigManager.getString("_debug", "false"));
            com.yoyoyo18.liteperf.ConfigManager.overrideBoolean("_debug", !prev);
            mc.player.sendMessage(new net.minecraft.text.LiteralText("LitePerf debug=" + (!prev)), false);
        }
    }
}
