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

    // GERÇEK TEXTURE BOYUTU
    private static final int TEX_W = 612;
    private static final int TEX_H = 408;

    // HUD ÖLÇEK
    private static final float SCALE = 0.6f;

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.currentScreen != null) return;

            int x = 10;
            int y = 20;

            ctx.getMatrices().push();
            ctx.getMatrices().scale(SCALE, SCALE, 1f);

            int sx = (int) (x / SCALE);
            int sy = (int) (y / SCALE);

            // 🔹 BASE (GRİ)
            ctx.drawTexture(BASE, sx, sy, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);

            long window = mc.getWindow().getHandle();

            // 🔹 TUŞLAR (SADECE İLGİLİ ALAN)
            drawKey(ctx, window, GLFW.GLFW_KEY_W, 150, 130, 70, 70);
            drawKey(ctx, window, GLFW.GLFW_KEY_A, 80, 200, 70, 70);
            drawKey(ctx, window, GLFW.GLFW_KEY_S, 150, 200, 70, 70);
            drawKey(ctx, window, GLFW.GLFW_KEY_D, 220, 200, 70, 70);

            drawKey(ctx, window, GLFW.GLFW_KEY_SPACE, 150, 290, 220, 70);
            drawKey(ctx, window, GLFW.GLFW_KEY_LEFT_SHIFT, 20, 200, 120, 70);

            ctx.getMatrices().pop();
        });
    }

    // 🔹 TUŞ BASILINCA SADECE O ALANI MAVİ ÇİZER
    private static void drawKey(
            DrawContext ctx,
            long window,
            int key,
            int texX,
            int texY,
            int w,
            int h
    ) {
        if (InputUtil.isKeyPressed(window, key)) {
            ctx.drawTexture(
                    ACTIVE,
                    texX,
                    texY,
                    texX,
                    texY,
                    w,
                    h,
                    TEX_W,
                    TEX_H
            );
        }
    }
}
