package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHp = -1f;
    private static float lastHp = -1f;
    private static float anim = 0f;
    private static float damageFlash = 0f;

    // 🟢 Fotoğrafa yakın yeşil-sarı gradient
    private static int gradient(float t, float o) {
        float r = 0.4f + 0.3f * (float)Math.sin(t + o);
        float g = 0.8f + 0.2f * (float)Math.sin(t + o + 2);
        float b = 0.3f + 0.15f * (float)Math.sin(t + o + 4);
        return 0xFF000000
                | ((int)(r * 255) << 16)
                | ((int)(g * 255) << 8)
                | (int)(b * 255);
    }

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) {
                smoothHp = -1;
                lastHp = -1;
                return;
            }

            Entity ent = ehr.getEntity();
            if (!(ent instanceof LivingEntity living)) return;

            float hp = living.getHealth();
            float max = living.getMaxHealth();

            if (smoothHp < 0) {
                smoothHp = hp;
                lastHp = hp;
            }

            if (hp < lastHp) damageFlash = 1f;
            lastHp = hp;

            float speed = hp < smoothHp ? 0.28f : 0.12f;
            smoothHp += (hp - smoothHp) * speed;

            anim += 0.04f;
            damageFlash *= 0.82f;

            // 📐 ORTA BOY – FOTOĞRAF ORANI
            int x = 10;
            int y = 20;
            int w = 140;
            int h = 34;

            // 🌫️ Arka plan
            ctx.fill(x, y, x + w, y + h, 0xAA000000);

            // 🧑 SKIN YÜZÜ – TAM ORTA (8x8 → 16x16)
            Identifier skin = mc.getEntityRenderDispatcher()
                    .getRenderer(living)
                    .getTexture(living);

            ctx.drawTexture(
                    skin,
                    x + 6, y + 6,
                    8, 8,
                    16, 16,
                    64, 64
            );

            // ✍️ Nick
            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    living.getName().getString(),
                    x + 28,
                    y + 4,
                    0xFFFFFFFF
            );

            // ❤️ HP yazısı
            String hpText = String.format(Locale.US, "%.1f / %.1f", smoothHp, max);
            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    hpText,
                    x + 28,
                    y + 15,
                    0xFFDDDDDD
            );

            // ❤️ HP BAR
            int barX = x + 28;
            int barY = y + h - 7;
            int barW = w - 34;

            ctx.fill(barX, barY, barX + barW, barY + 4, 0xFF1F1F1F);

            int filled = (int)(barW * (smoothHp / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(
                        barX + i,
                        barY,
                        barX + i + 1,
                        barY + 4,
                        gradient(anim, i * 0.18f)
                );
            }

            // 💥 Sarımsı hasar flash (fotoğraftaki gibi)
            if (damageFlash > 0.05f) {
                int a = (int)(damageFlash * 80);
                ctx.fill(
                        x, y, x + w, y + h,
                        (a << 24) | 0xFFFFCC33
                );
            }
        });
    }
}
