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

import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHealth = -1f;
    private static float lastHealth = -1f;
    private static float anim = 0f;
    private static float damageFlash = 0f;

    private static int waveColor(float t, float offset) {
        float r = 0.55f + 0.45f * (float)Math.sin(t + offset);
        float g = 0.55f + 0.45f * (float)Math.sin(t + offset + 2.2);
        float b = 0.55f + 0.45f * (float)Math.sin(t + offset + 4.4);
        return 0xFF000000 | ((int)(r*255)<<16) | ((int)(g*255)<<8) | (int)(b*255);
    }

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) {
                smoothHealth = -1;
                lastHealth = -1;
                return;
            }

            Entity ent = ehr.getEntity();
            if (!(ent instanceof LivingEntity living)) return;

            float hp = living.getHealth();
            float max = living.getMaxHealth();

            if (smoothHealth < 0) {
                smoothHealth = hp;
                lastHealth = hp;
            }

            // 🩸 Hasar algılama
            if (hp < lastHealth) {
                damageFlash = 1f; // vurulunca flash
            }
            lastHealth = hp;

            // 🎯 YÖNLÜ animasyon (azalırken geri, artarken ileri)
            float speed = hp < smoothHealth ? 0.25f : 0.12f;
            smoothHealth += (hp - smoothHealth) * speed;

            anim += 0.035f;
            damageFlash *= 0.85f;

            // 📐 Mini boyutlar
            int w = 96;
            int h = 28;
            int x = 8;
            int y = 18;

            // 🌫️ Gölge
            ctx.fill(x, y, x + w, y + h, 0x88000000);

            // 🖤 Panel
            ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xFF101010);

            // ❤️ İnce can barı
            int barY = y + h - 7;
            int innerW = w - 6;

            ctx.fill(x + 3, barY, x + w - 3, barY + 4, 0xFF2A2A2A);

            int filled = (int)(innerW * (smoothHealth / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(
                        x + 3 + i,
                        barY,
                        x + 4 + i,
                        barY + 4,
                        waveColor(anim, i * 0.15f)
                );
            }

            // 💥 Hasar flash overlay
            if (damageFlash > 0.05f) {
                int alpha = (int)(damageFlash * 120);
                ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, (alpha << 24) | 0x880000);
            }

            // ⚠️ Düşük can nabız
            if (smoothHealth / max < 0.25f) {
                float pulse = 0.5f + 0.5f * (float)Math.sin(anim * 3);
                int a = (int)(pulse * 60);
                ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, (a << 24) | 0xFF0000);
            }

            // 💖 Can yazısı – TAM ORTA
            String hpText = String.format(Locale.US, "%.0f/%.0f", smoothHealth, max);
            int tw = mc.textRenderer.getWidth(hpText);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + w/2 - tw/2, y + 7, 0xFFFFFFFF);

            // 📏 Mesafe (sağ alt – mini)
            double dist = mc.player.distanceTo(living);
            String d = String.format(Locale.US, "%.1fm", dist);
            int dw = mc.textRenderer.getWidth(d);
            ctx.drawTextWithShadow(mc.textRenderer, d, x + w - dw - 4, y + 2, 0xFFBBBBBB);
        });
    }
}
