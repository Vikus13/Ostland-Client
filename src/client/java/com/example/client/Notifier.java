package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/** Small popups in the bottom right corner when a module is switched. */
public final class Notifier {
    private record Note(String text, boolean on, long time) {}

    private static final List<Note> NOTES = new ArrayList<>();

    private Notifier() {}

    public static void push(String module, boolean on) {
        if (!Modules.NOTIFICATIONS.enabled) return;
        NOTES.add(new Note(module + (on ? " enabled" : " disabled"), on, System.currentTimeMillis()));
        while (NOTES.size() > 5) NOTES.remove(0);
    }

    public static void render(GuiGraphics g, Minecraft mc) {
        if (NOTES.isEmpty()) return;
        long now = System.currentTimeMillis();
        NOTES.removeIf(n -> now - n.time() > 2400);
        Font f = mc.font;
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int idx = 0;
        for (int i = NOTES.size() - 1; i >= 0; i--) {
            Note n = NOTES.get(i);
            long age = now - n.time();
            float a = 1f;
            float slide = 0f;
            if (age < 200) {
                a = age / 200f;
                slide = (1f - a) * 24f;
            } else if (age > 2000) {
                a = Math.max(0f, 1f - (age - 2000) / 400f);
            }
            int w = f.width(n.text()) + 28;
            int h = 20;
            int x = (int) (sw - w - 10 + slide);
            int y = sh - 44 - idx * 24;
            Gfx.alphaMul = a;
            Gfx.panel(g, x, y, w, h, 8);
            int dot = n.on() ? 0xFF58F58A : 0xFFFF5F5F;
            Gfx.rounded(g, x + 7, y + 7, 6, 6, 3, dot, dot);
            Gfx.text(g, f, n.text(), x + 19, y + 6, 0xFFFFFFFF);
            Gfx.alphaMul = 1f;
            idx++;
        }
    }
}
