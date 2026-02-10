package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static final int COLOR_IDLE = 0xAA2E3F1F; // koyu yeşil
    private static final int COLOR_ACTIVE = 0xFF2A6CFF; // MAVİ (basılı)
    private static final int BORDER = 0xFFFFFFFF;

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.currentScreen != null) return;

            int x = 10;
            int y = 20;
            int key = 18;
            int gap = 3;

            // Tuş çizme helper
            drawKey(ctx, mc, x + key, y + key, key, "W", GLFW.GLFW_KEY_W);
            drawKey(ctx, mc, x, y + key * 2 + gap, key, "A", GLFW.GLFW_KEY_A);
            drawKey(ctx, mc, x + key + gap, y + key * 2 + gap, key, "S", GLFW.GLFW_KEY_S);
            drawKey(ctx, mc, x + (key + gap) * 2, y + key * 2 + gap, key, "D", GLFW.GLFW_KEY_D);

            drawWideKey(ctx, mc, x, y + key * 3 + gap * 2, key * 3 + gap * 2, "SHIFT", GLFW.GLFW_KEY_LEFT_SHIFT);
            drawWideKey(ctx, mc, x, y + key * 4 + gap * 3, key * 4 + gap * 3, "SPACE", GLFW.GLFW_KEY_SPACE);

            // Mouse
            int mx = x + key * 4 + 20;
            int my = y + key;

            drawMouse(ctx, mc, mx, my, true);
            drawMouse(ctx, mc, mx + 26, my, false);
        });
    }

    private static void drawKey(DrawContext ctx, MinecraftClient mc, int x, int y, int s, String text, int keyCode) {
        boolean pressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), keyCode);
        ctx.fill(x, y, x + s, y + s, pressed ? COLOR_ACTIVE : COLOR_IDLE);
        drawBorder(ctx, x, y, s, s);
        ctx.drawTextWithShadow(mc.textRenderer, text, x + 6, y + 5, 0xFFFFFFFF);
    }

    private static void drawWideKey(DrawContext ctx, MinecraftClient mc, int x, int y, int w, String text, int keyCode) {
        boolean pressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), keyCode);
        ctx.fill(x, y, x + w, y + 18, pressed ? COLOR_ACTIVE : COLOR_IDLE);
        drawBorder(ctx, x, y, w, 18);
        ctx.drawTextWithShadow(mc.textRenderer, text, x + 6, y + 5, 0xFFFFFFFF);
    }

    private static void drawMouse(DrawContext ctx, MinecraftClient mc, int x, int y, boolean left) {
        boolean pressed = left ? mc.options.attackKey.isPressed() : mc.options.useKey.isPressed();
        ctx.fill(x, y, x + 22, y + 32, pressed ? COLOR_ACTIVE : COLOR_IDLE);
        drawBorder(ctx, x, y, 22, 32);
    }

    private static void drawBorder(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + 1, BORDER);
        ctx.fill(x, y + h - 1, x + w, y + h, BORDER);
        ctx.fill(x, y, x + 1, y + h, BORDER);
        ctx.fill(x + w - 1, y, x + w, y + h, BORDER);
    }
}
