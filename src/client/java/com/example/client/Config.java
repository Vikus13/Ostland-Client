package com.example.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Saves module states and settings to config/ostlandclient.properties. */
public final class Config {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("ostlandclient.properties");

    private Config() {}

    public static void load() {
        Properties p = new Properties();
        try (var in = Files.newInputStream(FILE)) {
            p.load(in);
        } catch (Exception e) {
            return; // no config yet: defaults
        }
        for (Module m : Modules.ALL) {
            String k = "m." + m.key() + ".";
            String on = p.getProperty(k + "on");
            if (on != null) m.enabled = Boolean.parseBoolean(on);
            for (Setting s : m.settings) read(p, k + s.key(), s);
        }
        for (Setting s : Theme.ALL) read(p, "theme." + s.key(), s);
    }

    private static void read(Properties p, String key, Setting s) {
        String v = p.getProperty(key);
        if (v == null) return;
        try {
            if (s.isBool) s.b = Boolean.parseBoolean(v);
            else s.v = Math.max(s.min, Math.min(s.max, Double.parseDouble(v)));
        } catch (NumberFormatException ignored) {
        }
    }

    public static void save() {
        Properties p = new Properties();
        for (Module m : Modules.ALL) {
            String k = "m." + m.key() + ".";
            p.setProperty(k + "on", String.valueOf(m.enabled));
            for (Setting s : m.settings) write(p, k + s.key(), s);
        }
        for (Setting s : Theme.ALL) write(p, "theme." + s.key(), s);
        try (var out = Files.newOutputStream(FILE)) {
            p.store(out, "Ostland Client");
        } catch (IOException ignored) {
        }
    }

    private static void write(Properties p, String key, Setting s) {
        p.setProperty(key, s.isBool ? String.valueOf(s.b) : String.valueOf(s.v));
    }
}
