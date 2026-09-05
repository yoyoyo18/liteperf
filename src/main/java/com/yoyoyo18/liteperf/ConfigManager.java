package com.yoyoyo18.liteperf;

import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final String RESOURCE = "/liteperf.toml";
    private static final Map<String, String> values = new HashMap<>();

    public static void loadDefault() {
        // First, try to load from config directory
        try {
            Path configDir = FabricLoader.getInstance().getConfigDir();
            Path cfg = configDir.resolve("liteperf.toml");
            if (Files.exists(cfg)) {
                try (BufferedReader br = Files.newBufferedReader(cfg, StandardCharsets.UTF_8)) {
                    parse(br);
                    values.put("_source", "config_dir");
                    return;
                }
            }
        } catch (Throwable t) {
            // ignore and fallback to resource
        }

        // Fallback: load packaged resource
        try (InputStream in = ConfigManager.class.getResourceAsStream(RESOURCE)) {
            if (in == null) return;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                parse(br);
                values.put("_source", "embedded");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parse(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("[")) continue;
            int eq = line.indexOf('=');
            if (eq <= 0) continue;
            String key = line.substring(0, eq).trim();
            String val = line.substring(eq + 1).trim();
            // remove quotes if present
            if (val.startsWith("\"") && val.endsWith("\"")) {
                val = val.substring(1, val.length() - 1);
            }
            // normalize key names (remove section prefixes if present)
            key = key.replace("render.", "");
            key = key.replace("tick.", "");
            key = key.replace("compat.", "compat.");
            values.put(key, val);
        }
    }

    public static boolean getBoolean(String key, boolean def) {
        String v = values.get(key);
        if (v == null) return def;
        return v.equalsIgnoreCase("true") || v.equals("1");
    }

    public static double getDouble(String key, double def) {
        String v = values.get(key);
        if (v == null) return def;
        try { return Double.parseDouble(v); } catch (NumberFormatException e) { return def; }
    }

    public static String getString(String key, String def) {
        return values.getOrDefault(key, def);
    }

    // Allow runtime overrides by the mod to enforce compatibility decisions
    public static void overrideBoolean(String key, boolean value) {
        values.put(key, Boolean.toString(value));
    }
}
