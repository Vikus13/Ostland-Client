package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Base class of every module (something you can switch on and off in the Click GUI). */
public abstract class Module {
    public final String name;
    public final String category;
    public final String desc;
    public boolean enabled;
    /** Only used by the Click GUI: are the settings of this module shown? */
    public boolean expanded;
    public final List<Setting> settings = new ArrayList<>();

    protected Module(String name, String category, String desc, boolean enabled) {
        this.name = name;
        this.category = category;
        this.desc = desc;
        this.enabled = enabled;
    }

    protected Setting bool(String n, boolean def) {
        Setting s = Setting.bool(n, def);
        settings.add(s);
        return s;
    }

    protected Setting num(String n, double def, double min, double max, double step) {
        Setting s = Setting.num(n, def, min, max, step);
        settings.add(s);
        return s;
    }

    public String key() {
        return name.toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    public void setEnabled(boolean on) {
        if (on == enabled) return;
        enabled = on;
        Minecraft mc = Minecraft.getInstance();
        if (on) onEnable(mc);
        else onDisable(mc);
        Notifier.push(name, on);
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void onEnable(Minecraft mc) {}

    public void onDisable(Minecraft mc) {}

    /** Called every client tick while the module is on and you are in a world. */
    public void tick(Minecraft mc) {}

    /** Called every frame while the module is on and you are in a world. */
    public void renderHud(GuiGraphics g, Minecraft mc) {}
}
