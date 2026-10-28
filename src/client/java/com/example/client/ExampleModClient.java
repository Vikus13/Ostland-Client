package com.example.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public class ExampleModClient implements ClientModInitializer {

    // ---------- settings (saved in config/ostlandclient.properties) ----------
    private static final Path CONFIG =
            FabricLoader.getInstance().getConfigDir().resolve("ostlandclient.properties");
    private static int colorRgb = 0x101010; // bar color (RGB)
    private static int opacity = 60;        // 0..100 (%)

    // ---------- TPS estimate ----------
    private static long lastTimePacket = 0;
    private static volatile double tps = -1;

    @Override
    public void onInitializeClient() {
        load();

        HudRenderCallback.EVENT.register((graphics, tickCounter) -> render(graphics));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            lastTimePacket = 0;
            tps = -1;
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("ostland")
                        .then(ClientCommandManager.literal("color")
                                .then(ClientCommandManager.argument("hex", StringArgumentType.word())
                                        .executes(c -> {
                                            String s = StringArgumentType.getString(c, "hex");
                                            try {
                                                if (s.length() != 6) throw new NumberFormatException();
                                                colorRgb = Integer.parseInt(s, 16);
                                                save();
                                                c.getSource().sendFeedback(Component.literal(
                                                        "Ostland Client color: #" + s.toUpperCase(Locale.ROOT)));
                                                return 1;
                                            } catch (NumberFormatException ex) {
                                                c.getSource().sendError(Component.literal(
                                                        "Use 6 hex digits, for example: /ostland color ff5555"));
                                                return 0;
                                            }
                                        })))
                        .then(ClientCommandManager.literal("opacity")
                                .then(ClientCommandManager.argument("percent", IntegerArgumentType.integer(0, 100))
                                        .executes(c -> {
                                            opacity = IntegerArgumentType.getInteger(c, "percent");
                                            save();
                                            c.getSource().sendFeedback(Component.literal(
                                                    "Ostland Client opacity: " + opacity + "%"));
                                            return 1;
                                        })))));
    }

    /** Called from TpsMixin every time the server sends a time update (every 20 server ticks). */
    public static void onTimeUpdate() {
        long now = System.nanoTime();
        if (lastTimePacket != 0) {
            double seconds = (now - lastTimePacket) / 1_000_000_000.0;
            // ignore tiny gaps and huge gaps (pause / hiccup)
            if (seconds > 0.05 && seconds < 5.0) {
                double inst = Math.min(20.0, 20.0 / seconds);
                double old = tps;
                tps = old < 0 ? inst : old * 0.5 + inst * 0.5;
            }
        }
        lastTimePacket = now;
    }

    // ---------- rendering ----------
    private static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        Font font = mc.font;

        int ping = -1;
        ClientPacketListener conn = mc.getConnection();
        if (conn != null) {
            PlayerInfo info = conn.getPlayerInfo(mc.player.getUUID());
            if (info != null) ping = info.getLatency();
        }
        double t = tps;

        Component[] parts = {
                Component.literal("Ostland Client").withStyle(ChatFormatting.BOLD),
                Component.literal(mc.player.getName().getString()),
                Component.literal(mc.getFps() + " FPS"),
                Component.literal(ping < 0 ? "-- ms" : ping + " ms"),
                Component.literal(t < 0 ? "-- TPS" : String.format(Locale.ROOT, "%.1f TPS", t))
        };

        final int pad = 10;
        final int gap = 6;
        final int h = 20;

        int total = pad * 2;
        for (int i = 0; i < parts.length; i++) {
            total += font.width(parts[i]);
            if (i < parts.length - 1) total += gap * 2 + 1;
        }

        int x = (mc.getWindow().getGuiScaledWidth() - total) / 2;
        int y = 4;

        int alpha = Math.round(opacity * 255f / 100f);
        int argb = (alpha << 24) | (colorRgb & 0xFFFFFF);
        fillRounded(g, x, y, total, h, h / 2, argb);

        int cx = x + pad;
        int textY = y + (h - 8) / 2;
        for (int i = 0; i < parts.length; i++) {
            g.drawString(font, parts[i], cx, textY, 0xFFFFFFFF, true);
            cx += font.width(parts[i]);
            if (i < parts.length - 1) {
                cx += gap;
                g.fill(cx, y + 5, cx + 1, y + h - 5, 0x55FFFFFF);
                cx += 1 + gap;
            }
        }
    }

    /** Rectangle with rounded corners, drawn row by row. */
    private static void fillRounded(GuiGraphics g, int x, int y, int w, int h, int radius, int argb) {
        radius = Math.min(radius, h / 2);
        for (int i = 0; i < h; i++) {
            int inset = 0;
            int d = -1;
            if (i < radius) {
                d = radius - i;
            } else if (i >= h - radius) {
                d = i - (h - radius) + 1;
            }
            if (d > 0) {
                double dd = d - 0.5;
                inset = (int) Math.round(radius - Math.sqrt(radius * radius - dd * dd));
            }
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, argb);
        }
    }

    // ---------- config ----------
    private static void load() {
        try (var in = Files.newInputStream(CONFIG)) {
            Properties p = new Properties();
            p.load(in);
            colorRgb = Integer.parseInt(p.getProperty("color", "101010"), 16);
            opacity = Math.max(0, Math.min(100, Integer.parseInt(p.getProperty("opacity", "60"))));
        } catch (Exception ignored) {
            // no config yet or broken file: use defaults
        }
    }

    private static void save() {
        try (var out = Files.newOutputStream(CONFIG)) {
            Properties p = new Properties();
            p.setProperty("color", String.format("%06X", colorRgb));
            p.setProperty("opacity", String.valueOf(opacity));
            p.store(out, "Ostland Client");
        } catch (IOException ignored) {
        }
    }
}
