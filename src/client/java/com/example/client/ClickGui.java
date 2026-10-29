package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/** The Click GUI: dark glass panels with all modules. Left click = switch, right click = settings. */
public class ClickGui extends Screen {
    private record Region(int x, int y, int w, int h, IntConsumer click, Setting slider) {}

    private final List<Region> regions = new ArrayList<>();
    private Region dragging;
    private double scroll = 0;
    private int contentH = 0;
    private final long openedAt = System.currentTimeMillis();
    private String hoverDesc = "";

    public ClickGui() {
        super(Component.literal("Ostland Client"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partialTick) {
        Font f = Minecraft.getInstance().font;
        regions.clear();
        hoverDesc = "";
        contentH = 0;
        Gfx.alphaMul = Math.min(1f, (System.currentTimeMillis() - openedAt) / 180f);
        Gfx.fill(g, 0, 0, width, height, 0x78000000);

        int n = Modules.CATEGORIES.length + 1;
        int gap = 10;
        int pw = Math.max(70, Math.min(124, (width - gap * (n + 1)) / n));
        int totalW = n * pw + (n - 1) * gap;
        int x0 = (width - totalW) / 2;
        int y0 = 36 + (int) scroll;

        Gfx.gradientText(g, f, "OSTLAND CLIENT", x0, 10, true, 0.05f);
        Gfx.text(g, f, "Left click: switch   Right click: settings   Right Shift / Esc: close", x0, 22, 0xFF9AA0B0);

        for (int c = 0; c < n; c++) {
            int px = x0 + c * (pw + gap);
            int h;
            if (c < Modules.CATEGORIES.length) h = drawCategory(g, f, Modules.CATEGORIES[c], px, y0, pw, mx, my);
            else h = drawTheme(g, f, px, y0, pw);
            contentH = Math.max(contentH, h + 70);
        }

        if (!hoverDesc.isEmpty()) {
            Gfx.text(g, f, hoverDesc, (width - f.width(hoverDesc)) / 2, height - 16, 0xFFC4C8D4);
        }
        Gfx.alphaMul = 1f;
    }

    private void header(GuiGraphics g, Font f, String title, int px, int py, int pw) {
        Gfx.textBold(g, f, title, px + (pw - Gfx.widthBold(f, title)) / 2, py + 6, 0xFFFFFFFF);
        Gfx.accentLine(g, px + 10, px + pw - 10, py + 17);
    }

    private int drawCategory(GuiGraphics g, Font f, String cat, int px, int py, int pw, int mx, int my) {
        List<Module> mods = new ArrayList<>();
        for (Module m : Modules.ALL) if (m.category.equals(cat)) mods.add(m);

        int h = 24;
        for (Module m : mods) {
            h += 16;
            if (m.expanded) h += m.settings.size() * 14 + 2;
        }
        Gfx.panel(g, px, py, pw, h, 6);
        header(g, f, cat, px, py, pw);

        int y = py + 20;
        for (Module m : mods) {
            boolean hover = mx >= px && mx < px + pw && my >= y && my < y + 16;
            if (hover) {
                hoverDesc = m.desc;
                Gfx.rounded(g, px + 3, y, pw - 6, 16, 4, 0x26FFFFFF, 0x26FFFFFF);
            }
            if (m.enabled) {
                Gfx.rounded(g, px + 3, y, pw - 6, 16, 4, Theme.accentA(0x66), Theme.accentA(0x33));
                Gfx.fill(g, px + 3, y + 3, px + 5, y + 13, Theme.accent());
            }
            Gfx.text(g, f, m.name, px + 10, y + 4, m.enabled ? 0xFFFFFFFF : 0xFFA8ADBA);
            if (!m.settings.isEmpty()) {
                Gfx.text(g, f, m.expanded ? "v" : ">", px + pw - 12, y + 4, 0xFF8890A0);
            }
            regions.add(new Region(px, y, pw, 16, b -> {
                if (b == 0) m.toggle();
                else if (b == 1) m.expanded = !m.expanded;
            }, null));
            y += 16;
            if (m.expanded) {
                for (Setting s : m.settings) {
                    drawSetting(g, f, s, px + 6, y, pw - 12);
                    y += 14;
                }
                y += 2;
            }
        }
        return h;
    }

    private int drawTheme(GuiGraphics g, Font f, int px, int py, int pw) {
        int h = 24 + Theme.ALL.size() * 14;
        Gfx.panel(g, px, py, pw, h, 6);
        header(g, f, "Theme", px, py, pw);
        int y = py + 22;
        for (Setting s : Theme.ALL) {
            drawSetting(g, f, s, px + 6, y, pw - 12);
            y += 14;
        }
        return h;
    }

    private void drawSetting(GuiGraphics g, Font f, Setting s, int x, int y, int w) {
        if (s.isBool) {
            Gfx.text(g, f, s.name, x + 3, y + 3, 0xFFD0D4DE);
            int sw = 20, sh = 8;
            int sx = x + w - sw - 3, sy = y + 3;
            int bg = s.b ? Theme.accentA(0xFF) : 0x55FFFFFF;
            Gfx.rounded(g, sx, sy, sw, sh, 4, bg, bg);
            int kx = s.b ? sx + sw - sh : sx;
            Gfx.rounded(g, kx + 1, sy + 1, sh - 2, sh - 2, 3, 0xFFFFFFFF, 0xFFFFFFFF);
            regions.add(new Region(x, y, w, 14, b -> {
                if (b == 0) s.b = !s.b;
            }, null));
        } else {
            Gfx.rounded(g, x, y + 1, w, 12, 4, 0x33FFFFFF, 0x33FFFFFF);
            int fw = (int) Math.round(w * s.fraction());
            if (fw > 0) Gfx.rounded(g, x, y + 1, Math.max(fw, 4), 12, 4, Theme.accentA(0xAA), Theme.accentA(0x70));
            Gfx.text(g, f, s.name, x + 4, y + 3, 0xFFFFFFFF);
            String val = s.display();
            Gfx.text(g, f, val, x + w - f.width(val) - 4, y + 3, 0xFFFFFFFF);
            regions.add(new Region(x, y + 1, w, 12, null, s));
        }
    }

    private void applySlider(double mx) {
        if (dragging == null || dragging.slider() == null) return;
        dragging.slider().setFraction((mx - dragging.x()) / (double) dragging.w());
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (int i = regions.size() - 1; i >= 0; i--) {
            Region r = regions.get(i);
            if (mx >= r.x() && mx < r.x() + r.w() && my >= r.y() && my < r.y() + r.h()) {
                if (r.slider() != null) {
                    dragging = r;
                    applySlider(mx);
                } else if (r.click() != null) {
                    r.click().accept(button);
                    Config.save();
                }
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging != null) {
            applySlider(mx);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (dragging != null) {
            dragging = null;
            Config.save();
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    // No @Override on purpose: if the signature ever differs, scrolling just stops working instead of breaking the build.
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        double min = Math.min(0, height - contentH);
        scroll = Math.max(min, Math.min(0, scroll + scrollY * 14));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        Config.save();
        super.onClose();
    }
}
