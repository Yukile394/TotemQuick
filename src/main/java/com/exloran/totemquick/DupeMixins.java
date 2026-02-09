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

    // Fotoğraftaki neon sarı-yeşil geçişi
    private static int gradient(float t, float o) {
        float g = 0.8f + 0.2f * (float)Math.sin(t + o);
        float r = 0.5f + 0.4f * (float)Math.sin(t + o + 1.5f);
        return 0xFF000000 | ((int)(r*255)<<16) | ((int)(g*255)<<8) | 40;
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
            if (hp < lastHp) damageFlash = 1.0f;
            lastHp = hp;

            smoothHp += (hp - smoothHp) * (hp < smoothHp ? 0.25f : 0.12f);
            anim += 0.05f;
            damageFlash = Math.max(0, damageFlash - 0.04f);

            // --- 📏 BOYUTLANDIRMA (SIKI VE TAM AYARLI) ---
            int x = 10;
            int y = 10;
            int w = 150;
            int h = 40; // İdeal yükseklik
            int s = 30; // Skin karesi boyutu

            // 🌫️ Arka Plan
            ctx.fill(x, y, x + w, y + h, 0xCC000000);

            // 🧑 SKIN (MİLLİMETRİK HİZALAMA)
            Identifier skin = mc.getEntityRenderDispatcher().getRenderer(living).getTexture(living);
            
            // Texture koordinatları (8,8 iç yüz - 40,8 dış katman) tam 64x64 ölçeğinde
            ctx.drawTexture(skin, x + 5, y + 5, 8, 8, s, s, 64, 64);
            ctx.drawTexture(skin, x + 5, y + 5, 40, 8, s, s, 64, 64);

            // 🔴 HASAR EFEKTİ (Sadece skin üzerine vuran flaş)
            if (damageFlash > 0) {
                int a = (int)(damageFlash * 170);
                ctx.fill(x + 5, y + 5, x + 5 + s, y + 5 + s, (a << 24) | 0xFF0000);
            }

            // ✍️ METİNLER
            String name = living.getName().getString().toLowerCase();
            ctx.drawTextWithShadow(mc.textRenderer, name, x + s + 12, y + 6, 0xFFFFFFFF);

            // HP Bilgisi (Biraz daha belirgin)
            String hpText = String.format(Locale.US, "%.1f / %.0f HP", hp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + s + 12, y + 16, 0xFFAAAAAA);

            // 🔋 CAN BARI (Kalın ve Sıkı)
            int barX = x + s + 12;
            int barY = y + 28;
            int barW = w - (s + 20);
            int barH = 7;

            ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF1A1A1A);

            int filled = (int)(barW * (Math.max(0, smoothHp) / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(barX + i, barY, barX + i + 1, barY + barH, gradient(anim, i * 0.1f));
            }

            // ✨ ŞAŞIRTICI DİNAMİK EFEKT
            // Rakibin canı azaldıkça barın etrafında beyaz bir parlama (aura) oluşur
            if (hp / max < 0.3f) {
                int auraAlpha = (int)(Math.abs(Math.sin(anim * 5)) * 60);
                ctx.fill(x, y, x + w, y + h, (auraAlpha << 24) | 0xFFFFFF);
            }
        });
    }
}
