package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static final Identifier BASE =
            Identifier.of("totemquick", "textures/gui/keyboard_hud.png");

    // 🔵 ACTIVE: SADECE MAVİ DOLU (tek renk)
    private static final Identifier ACTIVE =
            Identifier.of("totemquick", "textures/gui/keyboard_hud_active.png");

    // BASE texture boyutu
    private static final int TEX_W = 612;
    private static final int TEX_H = 408;

    static {
        HudRenderCallback.EVENT.register((ctx, tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.currentScreen != null) return;

            TotemQuickConfig cfg =
                    AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            if (!cfg.keyboardHudEnabled) return;

            ctx.getMatrices().push();
            ctx.getMatrices().scale(cfg.keyboardHudScale, cfg.keyboardHudScale, 1f);

            int x = (int) (cfg.keyboardHudX / cfg.keyboardHudScale);
            int y = (int) (cfg.keyboardHudY / cfg.keyboardHudScale);

            // === BASE (GRİ KLAVYE) ===
            ctx.drawTexture(BASE, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);

            long window = mc.getWindow().getHandle();

            // === KLAVYE ===
            key(ctx, window, GLFW.GLFW_KEY_W,     x + 150, y + 130, 70, 70);
            key(ctx, window, GLFW.GLFW_KEY_A,     x + 80,  y + 200, 70, 70);
            key(ctx, window, GLFW.GLFW_KEY_S,     x + 150, y + 200, 70, 70);
            key(ctx, window, GLFW.GLFW_KEY_D,     x + 220, y + 200, 70, 70);

            key(ctx, window, GLFW.GLFW_KEY_LEFT_SHIFT, x + 20,  y + 200, 120, 70);
            key(ctx, window, GLFW.GLFW_KEY_SPACE,      x + 150, y + 290, 220, 70);

            // === NUMARALAR ===
            key(ctx, window, GLFW.GLFW_KEY_1, x + 120, y + 60, 60, 60);
            key(ctx, window, GLFW.GLFW_KEY_2, x + 185, y + 60, 60, 60);
            key(ctx, window, GLFW.GLFW_KEY_3, x + 250, y + 60, 60, 60);

            // === MOUSE ===
            if (mc.options.attackKey.isPressed()) {
                drawActive(ctx, x + 420, y + 140, 80, 110); // sol tık
            }
            if (mc.options.useKey.isPressed()) {
                drawActive(ctx, x + 500, y + 140, 80, 110); // sağ tık
            }

            ctx.getMatrices().pop();
        });
    }

    private static void key(DrawContext ctx, long window, int key,
                            int x, int y, int w, int h) {
        if (InputUtil.isKeyPressed(window, key)) {
            drawActive(ctx, x, y, w, h);
        }
    }

    /**
     * 🔥 KRİTİK DÜZELTME:
     * UV HER ZAMAN 0,0 → texture taşması YOK
     */
    private static void drawActive(DrawContext ctx, int x, int y, int w, int h) {
        ctx.drawTexture(
                ACTIVE,
                x, y,           // ekranda çizilecek yer
                0, 0,           // UV BAŞLANGICI (SABİT)
                w, h,           // çizim boyutu
                w, h            // ACTIVE texture boyutu
        );
    }
            }
