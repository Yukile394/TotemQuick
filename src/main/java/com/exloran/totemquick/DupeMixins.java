package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;

public class DupeMixins {

    private static final Identifier TARGET =
            Identifier.of("totemquick", "textures/gui/target.png");

    public static void init() {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;

            int sw = mc.getWindow().getScaledWidth();
            int sh = mc.getWindow().getScaledHeight();
            int cx = sw / 2;
            int cy = sh / 2;

            long time = System.currentTimeMillis();
            float angle = (time % 10000L) / 10000f * 360f;

            ctx.getMatrices().push();
            ctx.getMatrices().translate(cx, cy, 0);
            ctx.getMatrices().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(angle));

            int size = 64; // büyük yapalım test için
            ctx.getMatrices().translate(-size / 2f, -size / 2f, 0);

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            ctx.drawTexture(TARGET, 0, 0, 0, 0, size, size, size, size);

            ctx.getMatrices().pop();
        });
    }
}
