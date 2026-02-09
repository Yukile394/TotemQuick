package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

import java.awt.*;
import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHealth = -1f;
    private static float t = 0f;

    private static int colorWave(float offset) {
        float r = 0.55f + 0.45f * (float)Math.sin(t + offset);
        float g = 0.55f + 0.45f * (float)Math.sin(t + offset + 2.1);
        float b = 0.55f + 0.45f * (float)Math.sin(t + offset + 4.2);
        return 0xFF000000 | ((int)(r*255)<<16) | ((int)(g*255)<<8) | (int)(b*255);
    }

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) {
                smoothHealth = -1;
                return;
            }

            Entity ent = ehr.getEntity();
            if (!(ent instanceof LivingEntity living)) return;

            float hp = living.getHealth();
            float max = living.getMaxHealth();

            if (smoothHealth < 0) smoothHealth = hp;
            smoothHealth += (hp - smoothHealth) * 0.15f;

            t += 0.035f;

            // 📏 Mini boyutlar
            int w = 92;
            int h = 26;
            int x = 8;
            int y = 18;

            // 🌫️ Hafif gölge
            ctx.fill(x, y, x + w, y + h, 0x88000000);

            // 🖤 Ana zemin
            ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xFF101010);

            // ❤️ İnce can barı
            int barY = y + h - 7;
            int barW = (int)((w - 6) * (smoothHealth / max));

            ctx.fill(x + 3, barY, x + w - 3, barY + 4, 0xFF2B2B2B);

            for (int i = 0; i < barW; i++) {
                ctx.fill(
                        x + 3 + i,
                        barY,
                        x + 4 + i,
                        barY + 4,
                        colorWave(i * 0.12f)
                );
            }

            // 💖 Can yazısı – BARIN İÇİNDE ORTA
            String hpText = String.format(Locale.US, "%.0f/%.0f", smoothHealth, max);
            int textW = mc.textRenderer.getWidth(hpText);

            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    hpText,
                    x + w / 2 - textW / 2,
                    y + 7,
                    0xFFFFFFFF
            );
        });
    }
}
