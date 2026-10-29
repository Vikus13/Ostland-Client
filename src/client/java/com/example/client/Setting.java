package com.example.client;

import java.util.Locale;

/** One adjustable value: either an on/off switch or a number slider. */
public final class Setting {
    public final String name;
    public final boolean isBool;
    public boolean b;
    public double v;
    public final double min, max, step;

    private Setting(String name, boolean isBool, boolean b, double v, double min, double max, double step) {
        this.name = name;
        this.isBool = isBool;
        this.b = b;
        this.v = v;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public static Setting bool(String name, boolean def) {
        return new Setting(name, true, def, 0, 0, 1, 1);
    }

    public static Setting num(String name, double def, double min, double max, double step) {
        return new Setting(name, false, false, def, min, max, step);
    }

    public double fraction() {
        if (max <= min) return 0;
        return Math.max(0, Math.min(1, (v - min) / (max - min)));
    }

    public void setFraction(double f) {
        f = Math.max(0, Math.min(1, f));
        double raw = min + f * (max - min);
        if (step > 0) raw = Math.round(raw / step) * step;
        v = Math.max(min, Math.min(max, raw));
    }

    public String display() {
        if (isBool) return b ? "ON" : "OFF";
        if (step >= 1) return String.valueOf((int) Math.round(v));
        return String.format(Locale.ROOT, "%.1f", v);
    }

    public String key() {
        return name.toLowerCase(Locale.ROOT).replace(' ', '_');
    }
}
