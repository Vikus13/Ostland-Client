package com.example.client;

import java.util.List;

/** Global look of everything: accent color, rainbow mode, glass opacity, corner radius. */
public final class Theme {
    public static final Setting R = Setting.num("Accent R", 124, 0, 255, 1);
    public static final Setting G = Setting.num("Accent G", 92, 0, 255, 1);
    public static final Setting B = Setting.num("Accent B", 255, 0, 255, 1);
    public static final Setting RAINBOW = Setting.bool("Rainbow", false);
    public static final Setting SPEED = Setting.num("Rainbow speed", 4, 1, 20, 1);
    public static final Setting OPACITY = Setting.num("Background", 72, 10, 100, 1);
    public static final Setting RADIUS = Setting.num("Corner radius", 10, 0, 14, 1);
    public static final List<Setting> ALL = List.of(R, G, B, RAINBOW, SPEED, OPACITY, RADIUS);

    private Theme() {}

    /** Accent color (opaque ARGB). With rainbow on it slowly cycles; offset shifts the hue. */
    public static int accent(float offset) {
        if (RAINBOW.b) {
            float h = (System.currentTimeMillis() % 20000L) / 20000f * (float) SPEED.v + offset;
            return Gfx.hsv(h, 0.6f, 1f);
        }
        return 0xFF000000 | ((int) R.v << 16) | ((int) G.v << 8) | (int) B.v;
    }

    public static int accent() {
        return accent(0f);
    }

    /** Accent color with a custom alpha (0..255). */
    public static int accentA(int alpha) {
        return (alpha << 24) | (accent() & 0xFFFFFF);
    }

    private static int bgAlpha() {
        return Math.round((float) OPACITY.v * 255f / 100f);
    }

    public static int bgTop() {
        return (bgAlpha() << 24) | 0x1E202C;
    }

    public static int bgBottom() {
        return (bgAlpha() << 24) | 0x0E0F16;
    }

    public static int radius(int h) {
        return Math.min(h / 2, (int) RADIUS.v);
    }
}
