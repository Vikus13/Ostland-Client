package com.example.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** Drawing helpers: rounded glass boxes, outlines, glow, gradient text. */
public final class Gfx {
    /** Global fade multiplier (used by fade-in / fade-out animations). */
    public static float alphaMul = 1f;

    private Gfx() {}

    private static int applyAlpha(int argb) {
        int a = (int) (((argb >>> 24) & 0xFF) * alphaMul);
        if (a < 0) a = 0;
        if (a > 255) a = 255;
        return (a << 24) | (argb & 0xFFFFFF);
    }

    public static void fill(GuiGraphics g, int x1, int y1, int x2, int y2, int argb) {
        if (x2 <= x1 || y2 <= y1) return;
        int c = applyAlpha(argb);
        if ((c >>> 24) == 0) return;
        g.fill(x1, y1, x2, y2, c);
    }

    public static int lerp(int c1, int c2, float t) {
        int a1 = (c1 >>> 24) & 0xFF, r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int a2 = (c2 >>> 24) & 0xFF, r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int gr = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (gr << 8) | b;
    }

    public static int hsv(float h, float s, float v) {
        h = h - (float) Math.floor(h);
        float x6 = h * 6f;
        int i = (int) x6;
        float f = x6 - i;
        float p = v * (1f - s);
        float q = v * (1f - f * s);
        float t = v * (1f - (1f - f) * s);
        float r, gr, b;
        switch (i % 6) {
            case 0: r = v; gr = t; b = p; break;
            case 1: r = q; gr = v; b = p; break;
            case 2: r = p; gr = v; b = t; break;
            case 3: r = p; gr = q; b = v; break;
            case 4: r = t; gr = p; b = v; break;
            default: r = v; gr = p; b = q; break;
        }
        return 0xFF000000 | ((int) (r * 255) << 16) | ((int) (gr * 255) << 8) | (int) (b * 255);
    }

    /** Horizontal inset of a rounded corner for row i of a box with height h. */
    private static double off(int r, int h, int i) {
        if (r <= 0) return 0;
        double dy = -1;
        if (i < r) dy = r - (i + 0.5);
        else if (i >= h - r) dy = (i + 0.5) - (h - r);
        if (dy < 0) return 0;
        return r - Math.sqrt(Math.max(0, r * (double) r - dy * dy));
    }

    /** Rounded box with a vertical gradient and smooth (anti-aliased) corners. */
    public static void rounded(GuiGraphics g, int x, int y, int w, int h, int r, int topArgb, int botArgb) {
        if (w <= 0 || h <= 0) return;
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        for (int i = 0; i < h; i++) {
            float t = h <= 1 ? 0f : (float) i / (h - 1);
            int c = lerp(topArgb, botArgb, t);
            double off = off(r, h, i);
            int full = (int) Math.ceil(off);
            fill(g, x + full, y + i, x + w - full, y + i + 1, c);
            if (full > off) {
                double cov = full - off;
                int pa = (int) (((c >>> 24) & 0xFF) * cov);
                int pc = (pa << 24) | (c & 0xFFFFFF);
                fill(g, x + full - 1, y + i, x + full, y + i + 1, pc);
                fill(g, x + w - full, y + i, x + w - full + 1, y + i + 1, pc);
            }
        }
    }

    /** One pixel outline following the rounded shape. */
    public static void ring(GuiGraphics g, int x, int y, int w, int h, int r, int argb) {
        if (w <= 2 || h <= 2) return;
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        for (int i = 0; i < h; i++) {
            int oo = (int) Math.round(off(r, h, i));
            if (i == 0 || i == h - 1) {
                fill(g, x + oo, y + i, x + w - oo, y + i + 1, argb);
                continue;
            }
            int oi = 1 + (int) Math.round(off(Math.max(0, r - 1), h - 2, i - 1));
            if (oi <= oo) oi = oo + 1;
            fill(g, x + oo, y + i, x + oi, y + i + 1, argb);
            fill(g, x + w - oi, y + i, x + w - oo, y + i + 1, argb);
        }
    }

    /** Soft glow / shadow around a box. */
    public static void glow(GuiGraphics g, int x, int y, int w, int h, int r, int rgb, int baseAlpha, int layers) {
        for (int k = layers; k >= 1; k--) {
            int a = baseAlpha * (layers - k + 1) / layers;
            int c = (a << 24) | (rgb & 0xFFFFFF);
            rounded(g, x - k, y - k, w + 2 * k, h + 2 * k, r + k, c, c);
        }
    }

    /** Dark glass panel: shadow, gradient, outline and a light top edge. */
    public static void panel(GuiGraphics g, int x, int y, int w, int h, int r) {
        glow(g, x, y, w, h, r, 0x000000, 30, 4);
        rounded(g, x, y, w, h, r, Theme.bgTop(), Theme.bgBottom());
        ring(g, x, y, w, h, r, 0x33FFFFFF);
        fill(g, x + r, y + 1, x + w - r, y + 2, 0x22FFFFFF);
    }

    /** Thin line colored with the accent (rainbow gradient when rainbow is on). */
    public static void accentLine(GuiGraphics g, int x1, int x2, int y) {
        for (int x = x1; x < x2; x += 3) {
            fill(g, x, y, Math.min(x + 3, x2), y + 1, Theme.accent((x - x1) * 0.012f));
        }
    }

    public static void text(GuiGraphics g, Font f, String s, int x, int y, int argb) {
        int c = applyAlpha(argb);
        if ((c >>> 24) < 8) return;
        g.drawString(f, Component.literal(s), x, y, c, true);
    }

    public static void textBold(GuiGraphics g, Font f, String s, int x, int y, int argb) {
        int c = applyAlpha(argb);
        if ((c >>> 24) < 8) return;
        MutableComponent comp = Component.literal(s);
        comp.withStyle(ChatFormatting.BOLD);
        g.drawString(f, comp, x, y, c, true);
    }

    public static int widthBold(Font f, String s) {
        MutableComponent comp = Component.literal(s);
        comp.withStyle(ChatFormatting.BOLD);
        return f.width(comp);
    }

    /** Draws text letter by letter in accent colors. With g == null it only measures. Returns the width. */
    public static int gradientText(GuiGraphics g, Font f, String s, int x, int y, boolean bold, float step) {
        int cx = x;
        for (int i = 0; i < s.length(); i++) {
            MutableComponent ch = Component.literal(String.valueOf(s.charAt(i)));
            if (bold) ch.withStyle(ChatFormatting.BOLD);
            if (g != null) {
                int c = applyAlpha(Theme.accent(i * step));
                if ((c >>> 24) >= 8) g.drawString(f, ch, cx, y, c, true);
            }
            cx += f.width(ch);
        }
        return cx - x;
    }
}
