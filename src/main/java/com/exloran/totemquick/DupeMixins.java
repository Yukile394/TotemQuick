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

    private static final int TEX_W = 612;
    private static final int TEX_H = 408;

    // 🔵 AKTİF TUŞ RENGİ (ŞIK MAVİ)
    private static final int ACTIVE_COLOR = 0xAA2A6CFF;

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

            // 🧱 BASE (GRİ HUD)
            ctx.drawTexture(BASE, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);

            long window = mc.getWindow().getHandle();

            // ==== KLAVYE ====
            key(ctx, window, GLFW.GLFW_KEY_W, 150, 130, 70, 70);
            key(ctx, window, GLFW.GLFW_KEY_A, 80, 200, 70, 70);
            key(ctx, window, GLFW.GLFW_KEY_S, 150, 200, 70, 70);
            key(ctx, window, GLFW.GLFW_KEY_D, 220, 200, 70, 70);

            key(ctx, window, GLFW.GLFW_KEY_LEFT_SHIFT, 20, 200, 120, 70);
            key(ctx, window, GLFW.GLFW_KEY_SPACE, 150, 290, 220, 70);

            // ==== NUMARALAR ====
            key(ctx, window, GLFW.GLFW_KEY_1, 120, 60, 60, 60);
            key(ctx, window, GLFW.GLFW_KEY_2, 185, 60, 60, 60);
            key(ctx, window, GLFW.GLFW_KEY_3, 250, 60, 60, 60);

            // ==== MOUSE ====
            if (mc.options.attackKey.isPressed()) {
                fill(ctx, 420, 140, 80, 110); // sol tık
            }
            if (mc.options.useKey.isPressed()) {
                fill(ctx, 500, 140, 80, 110); // sağ tık
            }

            ctx.getMatrices().pop();
        });
    }

    private static void key(DrawContext ctx, long window, int key,
                            int x, int y, int w, int h) {
        if (InputUtil.isKeyPressed(window, key)) {
            fill(ctx, x, y, w, h);
        }
    }

    // 🔵 SADECE TUŞ İÇİ RENK (TAŞMA YOK)
    private static void fill(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + h, ACTIVE_COLOR);
    }
}
