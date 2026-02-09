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

    // Fotoğraftaki canlı yeşil/sarı gradient yapısı
    private static int gradient(float t, float o) {
        float r = 0.5f + 0.5f * (float)Math.sin(t + o);
        float g = 0.9f + 0.1f * (float)Math.sin(t + o + 1);
        float b = 0.1f + 0.1f * (float)Math.sin(t + o + 2);
        return 0xFF000000 | ((int)(r*255)<<16) | ((int)(g*255)<<8) | (int)(b*255);
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

            float speed = hp < smoothHp ? 0.25f : 0.10f;
            smoothHp += (hp - smoothHp) * speed;

            anim += 0.04f;
            damageFlash *= 0.88f;

            // --- TASARIM AYARLARI ---
            int x = 10;
            int y = 20;
            int w = 140; // Genişlik
            int h = 42;  // Fotoğraftaki gibi biraz daha etli bir yapı
            int skinSize = 32; // Skin yüzü büyütüldü

            // 🌫️ Arka Plan (Yuvarlatılmış görünüm için iç içe fill)
            ctx.fill(x, y, x + w, y + h, 0xEE000000); 
            
            // 🧑 SKIN – YÜZ + OVERLAY (Daha net ve büyük)
            Identifier skin = mc.getEntityRenderDispatcher().getRenderer(living).getTexture(living);
            
            // Ana yüz katmanı
            ctx.drawTexture(skin, x + 5, y + 5, 8, 8, skinSize, skinSize, 64, 64);
            // Kafa üstü katmanı (saç/şapka) - Tam üzerine biner
            ctx.drawTexture(skin, x + 5, y + 5, 40, 8, skinSize, skinSize, 64, 64);

            // ✍️ Nick ve HP Bilgisi
            String name = living.getName().getString().toLowerCase(); // Fotoğraftaki gibi küçük harf stili
            ctx.drawTextWithShadow(mc.textRenderer, name, x + 42, y + 6, 0xFFFFFFFF);

            String hpText = String.format(Locale.US, "HP: %.1f (%.1f)", hp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + 42, y + 17, 0xFFAAAAAA);

            // ❤️ Can Barı (Fotoğraftaki kalın sarı bar)
            int barX = x + 42;
            int barY = y + 28;
            int barW = w - 50;
            int barH = 8;

            // Bar Arka Planı
            ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF1A1A1A);

            // Dolu Bar (Gradient)
            int filled = (int)(barW * (Math.max(0, smoothHp) / max));
            if (filled > 0) {
                for (int i = 0; i < filled; i++) {
                    ctx.fill(barX + i, barY, barX + i + 1, barY + barH, gradient(anim, i * 0.1f));
                }
            }

            // 💥 Hasar Efekti (Ekranı değil sadece paneli kaplar)
            if (damageFlash > 0.05f) {
                int alpha = (int)(damageFlash * 100);
                ctx.fill(x, y, x + w, y + h, (alpha << 24) | 0xFF0000);
            }
        });
    }
}
