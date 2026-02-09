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
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHp = -1;
    private static float lastHp = -1;
    private static float anim = 0;
    private static float damageFlash = 0;

    // Dinamik renk geçişi (Vurduğunda renkler kızıllaşır)
    private static int getDynamicColor(float t, float o, float flash) {
        float r = (0.4f + 0.4f * (float)Math.sin(t + o)) + (flash * 0.5f);
        float g = (0.8f + 0.2f * (float)Math.sin(t + o + 1.5f)) * (1.0f - flash);
        return 0xFF000000 | ((int)(Math.min(1, r)*255)<<16) | ((int)(Math.min(1, g)*255)<<8) | 50;
    }

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr) || !(ehr.getEntity() instanceof LivingEntity living)) {
                smoothHp = -1;
                lastHp = -1;
                return;
            }

            float hp = living.getHealth();
            float max = living.getMaxHealth();

            if (smoothHp < 0) { smoothHp = hp; lastHp = hp; }
            
            // 💥 VURUŞ EFEKTİ TETİKLEYİCİ
            if (hp < lastHp) damageFlash = 1.0f;
            lastHp = hp;

            smoothHp += (hp - smoothHp) * (hp < smoothHp ? 0.22f : 0.12f);
            anim += 0.05f;
            damageFlash = Math.max(0, damageFlash - 0.04f);

            // --- 📐 TASARIM ---
            int x = 12;
            int y = 12;
            int w = 150;
            int h = 38;
            int s = 28; // Kafa boyutu

            // Arka Plan
            ctx.fill(x, y, x + w, y + h, 0xDD000000);

            // 🧑 HATASIZ KAFA ÇİZİMİ (Skin Texture Fix)
            Identifier skin = mc.getEntityRenderDispatcher().getRenderer(living).getTexture(living);
            
            // Texture koordinatlarını 64x64 ölçeğinde sadece kafaya (8,8) kilitledim
            // Region: U=8, V=8, RegionWidth=8, RegionHeight=8
            ctx.drawTexture(skin, x + 5, y + 5, s, s, 8, 8, 8, 8, 64, 64);
            // Dış katman (saç/şapka): U=40, V=8
            ctx.drawTexture(skin, x + 5, y + 5, s, s, 40, 8, 8, 8, 64, 64);

            // 🔴 HASAR ANINDA KAFA PARLAMASI
            if (damageFlash > 0) {
                int a = (int)(damageFlash * 180);
                ctx.fill(x + 5, y + 5, x + 5 + s, y + 5 + s, (a << 24) | 0xFF0000);
            }

            // ✍️ YAZILAR
            String name = living.getName().getString().toLowerCase();
            ctx.drawTextWithShadow(mc.textRenderer, name, x + s + 12, y + 5, 0xFFFFFFFF);

            String hpText = String.format(Locale.US, "hp: %.1f / %.0f", hp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + s + 12, y + 15, 0xFFAAAAAA);

            // 🔋 CAN BARI (Dinamik Renk Değişimli)
            int barX = x + s + 12;
            int barY = y + 26;
            int barW = w - (s + 20);
            int barH = 7;

            ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF1A1A1A);

            int filled = (int)(barW * (Math.max(0, smoothHp) / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(barX + i, barY, barX + i + 1, barY + barH, getDynamicColor(anim, i * 0.1f, damageFlash));
            }

            // ✨ ŞAŞIRTICI EFEKT: KRİTİK KALP ATIŞI
            // Rakibin canı %30'un altına inerse panel kenarlarında kırmızı bir nabız atar
            if (hp / max < 0.3f) {
                float pulse = (float)Math.abs(Math.sin(anim * 6));
                int pulseAlpha = (int)(pulse * 100);
                // Üst ve alt kenara ince kırmızı hat
                ctx.fill(x, y, x + w, y + 1, (pulseAlpha << 24) | 0xFF0000);
                ctx.fill(x, y + h - 1, x + w, y + h, (pulseAlpha << 24) | 0xFF0000);
            }
        });
    }
}
