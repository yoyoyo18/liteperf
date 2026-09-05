package com.yoyoyo18.liteperf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final String RESOURCE = "/liteperf.toml";
    private static final Map<String, String> values = new HashMap<>();

    public static void loadDefault() {
        try (InputStream in = ConfigManager.class.getResourceAsStream(RESOURCE)) {
            if (in == null) return;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
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
                    values.put(key, val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
