package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/** Ostland Client entrypoint: HUD modules, Click GUI (Right Shift) and the TPS estimate. */
public class ExampleModClient implements ClientModInitializer {

    public static KeyMapping openKey;

    // ---------- TPS estimate (fed by TpsMixin) ----------
    private static long lastTimePacket = 0;
    public static volatile double tps = -1;

    @Override
    public void onInitializeClient() {
        Config.load();

        openKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "Ostland Open GUI", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "Ostland Client"));

        HudRenderCallback.EVENT.register((g, tickCounter) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.options.hideGui) return;
            for (Module m : Modules.ALL) {
                if (m.enabled) m.renderHud(g, mc);
            }
            Notifier.render(g, mc);
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.consumeClick()) {
                if (mc.screen == null) mc.setScreen(new ClickGui());
            }
            if (mc.player == null) return;
            for (Module m : Modules.ALL) {
                if (m.enabled) m.tick(mc);
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            lastTimePacket = 0;
            tps = -1;
        });
    }

    /** Called from TpsMixin every time the server sends a time update (every 20 server ticks). */
    public static void onTimeUpdate() {
        long now = System.nanoTime();
        if (lastTimePacket != 0) {
            double seconds = (now - lastTimePacket) / 1_000_000_000.0;
            if (seconds > 0.05 && seconds < 5.0) {
                double inst = Math.min(20.0, 20.0 / seconds);
                double old = tps;
                tps = old < 0 ? inst : old * 0.5 + inst * 0.5;
            }
        }
        lastTimePacket = now;
    }
}
