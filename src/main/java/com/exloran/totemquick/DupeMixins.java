package com.exloran.totemquick;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Random;

public class DupeMixins {

    private static float smoothHp = -1f;
    private static float lastHp = -1f;
    private static float anim = 0f;
    private static float damageFlash = 0f;

    private static final Random RANDOM = new Random();
    private static final float colorSeed = RANDOM.nextFloat() * 10f;

    // ✅ 1.21 uyumlu Identifier
    private static final Identifier HEART =
            Identifier.of("totemquick", "textures/gui/heart.png");

    public static void init() {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;

            if (!(mc.crosshairTarget instanceof EntityHitResult ehr)) return;
            if (!(ehr.getEntity() instanceof LivingEntity entity)) return;

            float hp = entity.getHealth();
            float maxHp = entity.getMaxHealth();

            if (smoothHp < 0) {
                smoothHp = hp;
                lastHp = hp;
            }

            smoothHp += (hp - smoothHp) * 0.15f;

            if (hp < lastHp) {
                damageFlash = 1f;
            }

            damageFlash *= 0.9f;
            lastHp = hp;

            anim += tickDelta * 0.05f;

            int screenW = ctx.getScaledWindowWidth();
            int screenH = ctx.getScaledWindowHeight();

            int barWidth = 90;
            int barHeight = 8;

            int x = (screenW - barWidth) / 2;
            int y = screenH / 2 + 20;

            // Arkaplan
            ctx.fill(x - 2, y - 2, x + barWidth + 2, y + barHeight + 2, 0x90000000);

            int healthWidth = (int)((smoothHp / maxHp) * barWidth);

            for (int i = 0; i < healthWidth; i++) {
                int color = getDynamicColor(anim, i * 0.12f, damageFlash);
                ctx.fill(x + i, y, x + i + 1, y + barHeight, color);
            }

            // Kalp ikonu
            ctx.drawTexture(HEART, x - 10, y - 1, 0, 0, 8, 8, 8, 8);
        });
    }

    // ✅ Java 22 / Gradle 8.7 SAFE
    private static int getDynamicColor(float t, float o, float flash) {
        float r = 0.5f + 0.5f * (float) Math.sin(t + o + colorSeed);
        float g = 0.5f + 0.5f * (float) Math.sin(t + o + 2 + colorSeed);
        float b = 0.5f + 0.5f * (float) Math.sin(t + o + 4 + colorSeed);

        r = Math.min(1f, r + flash * 0.7f);
        g = Math.max(0f, g - flash * 0.4f);

        return 0xFF000000
                | ((int)(r * 255) << 16)
                | ((int)(g * 255) << 8)
                | (int)(b * 255);
    }
}
