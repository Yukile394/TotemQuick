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

    // TEXTURE
    private static final Identifier BASE =
            new Identifier("totemquick", "textures/gui/keyboard_hud.png");
    private static final Identifier ACTIVE =
            new Identifier("totemquick", "textures/gui/keyboard_hud_active.png");

    // Texture size
    private static final int TEX_W = 612;
    private static final int TEX_H = 408;

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.currentScreen != null) return;

            int x = 10;
            int y = 20;

            // 🧱 NORMAL HUD
            ctx.drawTexture(BASE, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);

            long window = mc.getWindow().getHandle();

            // ⌨️ KEYBOARD
            drawIfPressed(ctx, window, GLFW.GLFW_KEY_W, x, y);
            drawIfPressed(ctx, window, GLFW.GLFW_KEY_A, x, y);
            drawIfPressed(ctx, window, GLFW.GLFW_KEY_S, x, y);
            drawIfPressed(ctx, window, GLFW.GLFW_KEY_D, x, y);
            drawIfPressed(ctx, window, GLFW.GLFW_KEY_LEFT_SHIFT, x, y);
            drawIfPressed(ctx, window, GLFW.GLFW_KEY_SPACE, x, y);

            // 🖱️ MOUSE
            if (mc.options.attackKey.isPressed()) {
                ctx.drawTexture(ACTIVE, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);
            }
            if (mc.options.useKey.isPressed()) {
                ctx.drawTexture(ACTIVE, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);
            }
        });
    }

    private static void drawIfPressed(DrawContext ctx, long window, int key, int x, int y) {
        if (InputUtil.isKeyPressed(window, key)) {
            ctx.drawTexture(ACTIVE, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);
        }
    }
}
