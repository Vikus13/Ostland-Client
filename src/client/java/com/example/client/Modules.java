package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** All modules of Ostland Client. Only visual and quality-of-life things, nothing that gives an unfair advantage. */
public final class Modules {
    public static final String[] CATEGORIES = {"HUD", "Visual", "Utility"};
    public static final List<Module> ALL = new ArrayList<>();
    static final Notifications NOTIFICATIONS = new Notifications();

    static final int GREEN = 0xFF58F58A;
    static final int YELLOW = 0xFFFFD84D;
    static final int RED = 0xFFFF5F5F;
    static final int WHITE = 0xFFFFFFFF;
    static final int GRAY = 0xFFB0B4C0;

    static {
        ALL.add(new InfoBar());
        ALL.add(new Coordinates());
        ALL.add(new Speed());
        ALL.add(new Cps());
        ALL.add(new Clock());
        ALL.add(new Keystrokes());
        ALL.add(new ArmorHud());
        ALL.add(new ModuleList());
        ALL.add(new Fullbright());
        ALL.add(NOTIFICATIONS);
        ALL.add(new ToggleSprint());
        ALL.add(new AutoRespawn());
    }

    private Modules() {}

    // ------------------------------------------------------------------ HUD

    /** The main glass bar: title, nickname, FPS, ping (signal bars) and server TPS. */
    static final class InfoBar extends HudModule {
        private static final int H = 20, PAD = 9, GAP = 7;
        private final Setting title = bool("Title", true);
        private final Setting nick = bool("Nickname", true);
        private final Setting fps = bool("FPS", true);
        private final Setting ping = bool("Ping", true);
        private final Setting showTps = bool("TPS", true);
        private final Setting colors = bool("Color values", true);

        // kind: 0 = text, 1 = title, 2 = avatar + text, 3 = signal bars + text
        private record Seg(int kind, String text, int color, int level) {}

        InfoBar() {
            super("Info Bar", "Nickname, FPS, ping and server TPS in one glass bar", true, 50, 1);
        }

        private int pingMs(Minecraft mc) {
            ClientPacketListener conn = mc.getConnection();
            if (conn == null) return -1;
            PlayerInfo info = conn.getPlayerInfo(mc.player.getUUID());
            return info == null ? -1 : info.getLatency();
        }

        private List<Seg> segs(Minecraft mc) {
            List<Seg> l = new ArrayList<>();
            if (title.b) l.add(new Seg(1, "Ostland Client", 0, 0));
            if (nick.b) l.add(new Seg(2, mc.player.getName().getString(), WHITE, 0));
            if (fps.b) {
                int v = mc.getFps();
                int c = !colors.b ? WHITE : v >= 100 ? GREEN : v >= 50 ? YELLOW : RED;
                l.add(new Seg(0, v + " FPS", c, 0));
            }
            if (ping.b) {
                int p = pingMs(mc);
                int lvl = p < 0 ? 0 : p < 60 ? 4 : p < 120 ? 3 : p < 200 ? 2 : 1;
                int c = !colors.b ? WHITE : p < 0 ? GRAY : p < 80 ? GREEN : p < 150 ? YELLOW : RED;
                l.add(new Seg(3, p < 0 ? "-- ms" : p + " ms", c, lvl));
            }
            if (showTps.b) {
                double t = ExampleModClient.tps;
                int c = !colors.b ? WHITE : t < 0 ? GRAY : t >= 19 ? GREEN : t >= 15 ? YELLOW : RED;
                l.add(new Seg(0, t < 0 ? "-- TPS" : String.format(Locale.ROOT, "%.1f TPS", t), c, 0));
            }
            return l;
        }

        /** Lays everything out. With g == null it only measures. Returns the total width. */
        private int layout(Minecraft mc, GuiGraphics g) {
            Font f = mc.font;
            List<Seg> l = segs(mc);
            int x = PAD;
            int ty = (H - 8) / 2;
            for (int i = 0; i < l.size(); i++) {
                Seg s = l.get(i);
                int w;
                if (s.kind() == 1) {
                    w = Gfx.gradientText(g, f, s.text(), x, ty, true, 0.05f);
                } else if (s.kind() == 2) {
                    w = 16 + f.width(s.text());
                    if (g != null) {
                        int ay = (H - 12) / 2;
                        Gfx.rounded(g, x, ay, 12, 12, 4, Theme.accentA(0xFF), Theme.accentA(0xB0));
                        String letter = s.text().isEmpty() ? "?" : s.text().substring(0, 1).toUpperCase(Locale.ROOT);
                        Gfx.text(g, f, letter, x + (12 - f.width(letter)) / 2, ay + 2, 0xFF10121A);
                        Gfx.text(g, f, s.text(), x + 16, ty, s.color());
                    }
                } else if (s.kind() == 3) {
                    w = 15 + f.width(s.text());
                    if (g != null) {
                        for (int b = 0; b < 4; b++) {
                            int bh = 3 + b * 2;
                            int bx = x + b * 3;
                            int by = (H + 9) / 2 - bh;
                            Gfx.fill(g, bx, by, bx + 2, by + bh, b < s.level() ? s.color() : 0x40FFFFFF);
                        }
                        Gfx.text(g, f, s.text(), x + 15, ty, s.color());
                    }
                } else {
                    w = f.width(s.text());
                    if (g != null) Gfx.text(g, f, s.text(), x, ty, s.color());
                }
                x += w;
                if (i < l.size() - 1) {
                    x += GAP;
                    if (g != null) Gfx.fill(g, x, 5, x + 1, H - 5, 0x38FFFFFF);
                    x += 1 + GAP;
                }
            }
            return x + PAD;
        }

        @Override
        protected int width(Minecraft mc) {
            return layout(mc, null);
        }

        @Override
        protected int height(Minecraft mc) {
            return H;
        }

        @Override
        protected void draw(GuiGraphics g, Minecraft mc) {
            int w = layout(mc, null);
            box(g, w, H);
            int r = Theme.radius(H);
            Gfx.accentLine(g, r, w - r, H - 2);
            layout(mc, g);
        }
    }

    /** XYZ and the direction you are facing. */
    static final class Coordinates extends HudModule {
        private static final String[] DIRS = {"South (+Z)", "West (-X)", "North (-Z)", "East (+X)"};
        private final Setting facing = bool("Facing", true);

        Coordinates() {
            super("Coordinates", "XYZ position and facing direction", false, 1, 12);
        }

        private String line1(Minecraft mc) {
            return String.format(Locale.ROOT, "X %d   Y %d   Z %d",
                    (int) Math.floor(mc.player.getX()), (int) Math.floor(mc.player.getY()), (int) Math.floor(mc.player.getZ()));
        }

        private String line2(Minecraft mc) {
            float yaw = ((mc.player.getYRot() % 360f) + 360f) % 360f;
            return DIRS[(int) ((yaw + 45f) / 90f) % 4];
        }

        @Override
        protected int width(Minecraft mc) {
            Font f = mc.font;
            int w = f.width(line1(mc));
            if (facing.b) w = Math.max(w, f.width(line2(mc)));
            return w + 20;
        }

        @Override
        protected int height(Minecraft mc) {
            return facing.b ? 31 : 20;
        }

        @Override
        protected void draw(GuiGraphics g, Minecraft mc) {
            Font f = mc.font;
            int w = width(mc);
            int h = height(mc);
            box(g, w, h);
            Gfx.text(g, f, line1(mc), 10, 6, WHITE);
            if (facing.b) Gfx.text(g, f, line2(mc), 10, 18, Theme.accent());
        }
    }

    /** Your walking / flying speed in blocks per second. */
    static final class Speed extends HudModule {
        private double speed;
        private double lx, lz;
        private boolean has;

        Speed() {
            super("Speed", "Your speed in blocks per second", false, 1, 26);
        }

        @Override
        public void tick(Minecraft mc) {
            double x = mc.player.getX();
            double z = mc.player.getZ();
            if (has) {
                double d = Math.sqrt((x - lx) * (x - lx) + (z - lz) * (z - lz)) * 20.0;
                speed = speed * 0.6 + d * 0.4;
            }
            lx = x;
            lz = z;
            has = true;
        }

        @Override
        protected int width(Minecraft mc) {
            return mc.font.width("Speed 00.0 b/s") + 20;
        }

        @Override
        protected int height(Minecraft mc) {
            return 20;
        }

        @Override
        protected void draw(GuiGraphics g, Minecraft mc) {
            Font f = mc.font;
            box(g, width(mc), 20);
            Gfx.text(g, f, "Speed", 10, 6, Theme.accent());
            Gfx.text(g, f, String.format(Locale.ROOT, "%.1f b/s", speed), 10 + f.width("Speed "), 6, WHITE);
        }
    }

    /** Clicks per second (left | right). */
    static final class Cps extends HudModule {
        private final ArrayDeque<Long> left = new ArrayDeque<>();
        private final ArrayDeque<Long> right = new ArrayDeque<>();
        private boolean lastL, lastR;

        Cps() {
            super("CPS", "Clicks per second (left | right mouse button)", false, 1, 34);
        }

        private void poll(Minecraft mc) {
            long win = mc.getWindow().getWindow();
            boolean l = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            boolean r = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            long now = System.currentTimeMillis();
            if (l && !lastL) left.add(now);
            if (r && !lastR) right.add(now);
            lastL = l;
            lastR = r;
            while (!left.isEmpty() && now - left.peekFirst() > 1000L) left.pollFirst();
            while (!right.isEmpty() && now - right.peekFirst() > 1000L) right.pollFirst();
        }

        @Override
        protected int width(Minecraft mc) {
            return 10 + mc.font.width("CPS ") + mc.font.width("00 | 00") + 10;
        }

        @Override
        protected int height(Minecraft mc) {
            return 20;
        }

        @Override
        protected void draw(GuiGraphics g, Minecraft mc) {
            poll(mc);
            Font f = mc.font;
            box(g, width(mc), 20);
            Gfx.text(g, f, "CPS", 10, 6, Theme.accent());
            Gfx.text(g, f, left.size() + " | " + right.size(), 10 + f.width("CPS "), 6, WHITE);
        }
    }

    /** Real time clock. */
    static final class Clock extends HudModule {
        private static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("HH:mm");
        private static final DateTimeFormatter LONG = DateTimeFormatter.ofPattern("HH:mm:ss");
        private final Setting seconds = bool("Seconds", false);

        Clock() {
            super("Clock", "Your computer time", false, 100, 1);
        }

        private String now() {
            return LocalTime.now().format(seconds.b ? LONG : SHORT);
        }

        @Override
        protected int width(Minecraft mc) {
            return mc.font.width(now()) + 20;
        }

        @Override
        protected int height(Minecraft mc) {
            return 20;
        }

        @Override
        protected void draw(GuiGraphics g, Minecraft mc) {
            box(g, width(mc), 20);
            Gfx.text(g, mc.font, now(), 10, 6, WHITE);
        }
    }

    /** WASD, space and mouse buttons. Pressed keys light up in the accent color. */
    static final class Keystrokes extends HudModule {
        Keystrokes() {
            super("Keystrokes", "Shows pressed keys: WASD, space and mouse buttons", false, 1, 60);
        }

        private void key(GuiGraphics g, Font f, int x, int y, int w, int h, String label, boolean down) {
            if (down) Gfx.rounded(g, x, y, w, h, 4, Theme.accentA(0xE0), Theme.accentA(0xA0));
            else Gfx.rounded(g, x, y, w, h, 4, Theme.bgTop(), Theme.bgBottom());
            Gfx.ring(g, x, y, w, h, 4, down ? 0x55FFFFFF : 0x30FFFFFF);
            Gfx.text(g, f, label, x + (w - f.width(label)) / 2, y + (h - 8) / 2, down ? 0xFF10121A : WHITE);
        }

        @Override
        protected int width(Minecraft mc) {
            return 58;
        }

        @Override
        protected int height(Minecraft mc) {
            return 70;
        }

        @Override
        protected void draw(GuiGraphics g, Minecraft mc) {
            Font f = mc.font;
            var o = mc.options;
            key(g, f, 20, 0, 18, 18, "W", o.keyUp.isDown());
            key(g, f, 0, 20, 18, 18, "A", o.keyLeft.isDown());
            key(g, f, 20, 20, 18, 18, "S", o.keyDown.isDown());
            key(g, f, 40, 20, 18, 18, "D", o.keyRight.isDown());
            key(g, f, 0, 40, 58, 12, "SPACE", o.keyJump.isDown());
            key(g, f, 0, 54, 28, 16, "LMB", o.keyAttack.isDown());
            key(g, f, 30, 54, 28, 16, "RMB", o.keyUse.isDown());
        }
    }

    /** Armor and held item with remaining durability. */
    static final class ArmorHud extends HudModule {
        private static final EquipmentSlot[] SLOTS = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
        };
        private static final int SW = 24;

        ArmorHud() {
            super("Armor HUD", "Armor and held item with durability", false, 100, 55);
        }

        @Override
        protected int width(Minecraft mc) {
            return SLOTS.length * SW + 4;
        }

        @Override
        protected int height(Minecraft mc) {
            return 36;
        }

        @Override
        protected void draw(GuiGraphics g, Minecraft mc) {
            Font f = mc.font;
            box(g, width(mc), 36);
            for (int i = 0; i < SLOTS.length; i++) {
                ItemStack st = mc.player.getItemBySlot(SLOTS[i]);
                int sx = 2 + i * SW + (SW - 16) / 2;
                if (st.isEmpty()) {
                    Gfx.rounded(g, sx, 5, 16, 16, 4, 0x18FFFFFF, 0x18FFFFFF);
                    continue;
                }
                g.renderItem(st, sx, 5);
                g.renderItemDecorations(f, st, sx, 5);
                if (st.isDamageableItem()) {
                    int left = st.getMaxDamage() - st.getDamageValue();
                    float fr = (float) left / st.getMaxDamage();
                    String t = String.valueOf(left);
                    int c = fr > 0.5f ? GREEN : fr > 0.2f ? YELLOW : RED;
                    Gfx.text(g, f, t, 2 + i * SW + (SW - f.width(t)) / 2, 24, c);
                }
            }
        }
    }

    /** List of all enabled modules, right aligned, with rainbow-capable accent bars. */
    static final class ModuleList extends HudModule {
        ModuleList() {
            super("Module List", "Shows all enabled modules (right aligned)", false, 100, 14);
        }

        private List<String> names(Font f) {
            List<String> l = new ArrayList<>();
            for (Module m : ALL) if (m.enabled && m != this) l.add(m.name);
            l.sort((a, b) -> f.width(b) - f.width(a));
            return l;
        }

        @Override
        protected int width(Minecraft mc) {
            List<String> l = names(mc.font);
            return l.isEmpty() ? 1 : mc.font.width(l.get(0)) + 12;
        }

        @Override
        protected int height(Minecraft mc) {
            return Math.max(1, names(mc.font).size() * 13);
        }

        @Override
        protected void draw(GuiGraphics g, Minecraft mc) {
            Font f = mc.font;
            List<String> l = names(f);
            int total = width(mc);
            for (int i = 0; i < l.size(); i++) {
                String n = l.get(i);
                int rw = f.width(n) + 12;
                int x = total - rw;
                int y = i * 13;
                int c = Theme.accent(i * 0.06f);
                Gfx.rounded(g, x, y, rw, 12, 3, Theme.bgTop(), Theme.bgBottom());
                Gfx.fill(g, total - 2, y + 2, total, y + 10, c);
                Gfx.text(g, f, n, x + 5, y + 2, c);
            }
        }
    }

    // --------------------------------------------------------------- Visual

    /** Night vision look, client side only. */
    static final class Fullbright extends Module {
        private boolean added;

        Fullbright() {
            super("Fullbright", "Visual", "See clearly in the dark (night vision look, client side)", false);
        }

        @Override
        public void tick(Minecraft mc) {
            if (!mc.player.hasEffect(MobEffects.NIGHT_VISION)) {
                mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,
                        MobEffectInstance.INFINITE_DURATION, 0, false, false, false));
                added = true;
            }
        }

        @Override
        public void onDisable(Minecraft mc) {
            if (added && mc.player != null) mc.player.removeEffect(MobEffects.NIGHT_VISION);
            added = false;
        }
    }

    /** Popups when modules are switched (the work is done in Notifier). */
    static final class Notifications extends Module {
        Notifications() {
            super("Notifications", "Visual", "Small popups when you switch a module", true);
        }
    }

    // -------------------------------------------------------------- Utility

    /** Holds sprint for you while you walk forward. */
    static final class ToggleSprint extends Module {
        ToggleSprint() {
            super("Toggle Sprint", "Utility", "Sprint automatically while moving forward", false);
        }

        @Override
        public void tick(Minecraft mc) {
            if (mc.screen == null && mc.options.keyUp.isDown() && !mc.player.isShiftKeyDown()) {
                mc.options.keySprint.setDown(true);
            }
        }

        @Override
        public void onDisable(Minecraft mc) {
            mc.options.keySprint.setDown(false);
        }
    }

    /** Respawns you automatically after you die. */
    static final class AutoRespawn extends Module {
        private int cooldown;

        AutoRespawn() {
            super("Auto Respawn", "Utility", "Respawn automatically after dying", false);
        }

        @Override
        public void tick(Minecraft mc) {
            if (mc.player.isDeadOrDying()) {
                if (cooldown-- <= 0) {
                    mc.player.respawn();
                    cooldown = 20;
                }
            } else {
                cooldown = 0;
            }
        }
    }
}
