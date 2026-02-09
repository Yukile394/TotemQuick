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

    // Fotoğraftaki o keskin sarıdan yeşile geçen neon bar
    private static int getBarColor(float t, float offset) {
        float f = (float) Math.sin(t + offset * 0.2f);
        int r = (int) (180 + 75 * f); // Sarı tonu için kırmızı yüksek
        int g = 255;                  // Yeşil her zaman ful
        int b = 30;                   // Hafif sıcaklık
        return 0xFF000000 | (r << 16) | (g << 8) | b;
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

            // 💥 HASAR TEPKİSİ
            if (hp < lastHp) damageFlash = 1.0f;
            lastHp = hp;

            // Animasyon değerleri
            smoothHp += (hp - smoothHp) * (hp < smoothHp ? 0.22f : 0.10f);
            anim += 0.06f;
            damageFlash = Math.max(0, damageFlash - 0.05f);

            // --- 📐 GEOMETRİ (SIKI VE TAM KARE) ---
            int x = 8;
            int y = 8;
            int w = 150;
            int h = 34; // Daha dar ve kompakt yapı
            int s = 26; // Skin yüzü boyutu (Daha fit)

            // 🌫️ Arka Plan (Görseldeki gibi mat siyah)
            ctx.fill(x, y, x + w, y + h, 0xDD000000);
            
            // 🧑 SKIN (HATASIZ VE NET YÜZ)
            Identifier skin = mc.getEntityRenderDispatcher().getRenderer(living).getTexture(living);
            
            // İç Yüz (Netleştirilmiş 8x8 piksellik alan)
            ctx.drawTexture(skin, x + 4, y + 4, 8, 8, s, s, 64, 64);
            // Dış Katman (Overlay) - Saç ve Aksesuarlar
            ctx.drawTexture(skin, x + 4, y + 4, 40, 8, s, s, 64, 64);

            // 🟥 SKIN HASAR FLASH (Sadece yüze vuran kırmızı vuruş hissi)
            if (damageFlash > 0) {
                int alpha = (int) (damageFlash * 160);
                ctx.fill(x + 4, y + 4, x + 4 + s, y + 4 + s, (alpha << 24) | 0xFF0000);
            }

            // ✍️ METİNLER
            String name = living.getName().getString();
            ctx.drawTextWithShadow(mc.textRenderer, name, x + s + 10, y + 4, 0xFFFFFFFF);

            // HP Yazısı (Görseldeki gibi ufak ve gri tonlarda)
            String hpText = String.format(Locale.US, "hp: %.1f / %.0f", hp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + s + 10, y + 14, 0xFFBBBBBB);

            // 🔋 BAR YAPISI
            int barX = x + s + 10;
            int barY = y + 24;
            int barW = w - (s + 16);
            int barH = 6;

            // Boş Bar
            ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF111111);

            // Dolu Bar (Yumuşak geçişli)
            int filled = (int) (barW * (Math.max(0, smoothHp) / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(barX + i, barY, barX + i + 1, barY + barH, getBarColor(anim, i));
            }

            // ⭐ ŞAŞIRTICI DETAY: KRİTİK PARLAMA
            // Eğer vurduğun kişinin canı %20 altındaysa barın sonunda küçük bir "!" ikonu çıkar
            if (hp / max < 0.2f) {
                float pulse = (float) Math.abs(Math.sin(anim * 4));
                int pulseColor = ( (int)(pulse * 255) << 16 ) | 0x0000; 
                ctx.drawTextWithShadow(mc.textRenderer, "!", barX + barW + 2, barY - 2, 0xFFFF0000 | pulseColor);
            }
        });
    }
}
