package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static final Identifier BASE =
            Identifier.of("totemquick", "textures/gui/keyboard_hud.png");

    private static final Identifier ACTIVE =
            Identifier.of("totemquick", "textures/gui/keyboard_hud_active.png");

    private static final int TEX_W = 612;
    private static final int TEX_H = 408;

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.currentScreen != null) return;

            int x = 10;
            int y = 20;
            long window = mc.getWindow().getHandle();

            // 🧱 BASE – 1 KEZ
            ctx.drawTexture(BASE, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);

            // ⌨️ KLAVYE (bölgesel)
            key(ctx, window, GLFW.GLFW_KEY_W, 210, 95, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_A, 150, 160, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_S, 210, 160, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_D, 270, 160, 60, 60, x, y);

            key(ctx, window, GLFW.GLFW_KEY_Q, 150, 95, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_E, 270, 95, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_R, 330, 95, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_T, 390, 95, 60, 60, x, y);

            key(ctx, window, GLFW.GLFW_KEY_Z, 210, 225, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_X, 270, 225, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_C, 330, 225, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_V, 390, 225, 60, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_B, 450, 225, 60, 60, x, y);

            key(ctx, window, GLFW.GLFW_KEY_LEFT_SHIFT, 120, 225, 120, 60, x, y);
            key(ctx, window, GLFW.GLFW_KEY_SPACE, 210, 300, 300, 60, x, y);

            // 🖱️ MOUSE
            if (mc.options.attackKey.isPressed()) {
                drawPart(ctx, 500, 95, 50, 120, x, y);
            }
            if (mc.options.useKey.isPressed()) {
                drawPart(ctx, 555, 95, 50, 120, x, y);
            }
        });
    }

    // 🔑 Klavye helper
    private static void key(DrawContext ctx, long window, int key,
                            int u, int v, int w, int h,
                            int x, int y) {
        if (InputUtil.isKeyPressed(window, key)) {
            drawPart(ctx, u, v, w, h, x, y);
        }
    }

    // 🎯 Bölgesel çizim (GLITCH BURADA BİTER)
    private static void drawPart(DrawContext ctx,
                                 int u, int v, int w, int h,
                                 int x, int y) {
        ctx.drawTexture(
                ACTIVE,
                x + u, y + v,
                u, v,
                w, h,
                TEX_W, TEX_H
        );
    }
}
