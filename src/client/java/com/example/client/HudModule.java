package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** A module that draws something on the screen. Position and scale are adjustable. */
public abstract class HudModule extends Module {
    protected final Setting posX;
    protected final Setting posY;
    protected final Setting scale;

    protected HudModule(String name, String desc, boolean on, double x, double y) {
        super(name, "HUD", desc, on);
        posX = num("Pos X", x, 0, 100, 1);
        posY = num("Pos Y", y, 0, 100, 1);
        scale = num("Scale", 1.0, 0.5, 2.0, 0.1);
    }

    protected abstract int width(Minecraft mc);

    protected abstract int height(Minecraft mc);

    /** Draw at (0, 0); the position and scale are already applied. */
    protected abstract void draw(GuiGraphics g, Minecraft mc);

    protected void box(GuiGraphics g, int w, int h) {
        Gfx.panel(g, 0, 0, w, h, Theme.radius(h));
    }

    @Override
    public void renderHud(GuiGraphics g, Minecraft mc) {
        int w = width(mc);
        int h = height(mc);
        float s = (float) scale.v;
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        float px = Math.round((sw - w * s) * (float) posX.v / 100f);
        float py = Math.round((sh - h * s) * (float) posY.v / 100f);
        g.pose().pushPose();
        g.pose().translate(px, py, 0f);
        g.pose().scale(s, s, 1f);
        draw(g, mc);
        g.pose().popPose();
    }
}
