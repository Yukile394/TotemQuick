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
    private static float heartBeat = 0;

    private static int gradient(float t, float o) {
        // Fotoğraftaki o meşhur parlak sarı-yeşil geçişi
        float r = 0.4f + 0.4f * (float)Math.sin(t + o);
        float g = 0.8f + 0.2f * (float)Math.sin(t + o + 1.5f);
        return 0xFF000000 | ((int)(r*255)<<16) | ((int)(g*255)<<8) | 50;
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

            if (smoothHp < 0) { smoothHp = hp; lastHp = hp; }

            // 💥 HASAR TESPİTİ
            if (hp < lastHp) {
                damageFlash = 1.5f; // Skini kırmızıya boyayacak güç
                heartBeat = 1.0f;   // Can gidince bar titrer
            }
            lastHp = hp;

            // Yumuşatma animasyonları
            smoothHp += (hp - smoothHp) * (hp < smoothHp ? 0.25f : 0.12f);
            anim += 0.05f;
            damageFlash *= 0.85f;
            heartBeat *= 0.9f;

            // --- PANEL TASARIMI (SIKI VE ŞIK) ---
            int x = 10; 
            int y = 10;
            int w = 155; 
            int h = 40;
            int s = 30; // Skin boyutu

            // Arka Plan (Modern yarı saydam siyah)
            ctx.fill(x, y, x + w, y + h, 0xCC000000);
            ctx.fill(x, y, x + w, y + 1, 0x44FFFFFF); // Üst ince çizgi detayı

            // 🧑 SKIN (TAM YÜZ VE HATASIZ KATMAN)
            Identifier skin = mc.getEntityRenderDispatcher().getRenderer(living).getTexture(living);
            
            // Ana Yüz (8, 8 koordinatından 8x8 alan)
            ctx.drawTexture(skin, x + 5, y + 5, 8, 8, s, s, 64, 64);
            // Dış Katman/Kask (40, 8 koordinatından 8x8 alan) - Tam hizalama
            ctx.drawTexture(skin, x + 5, y + 5, 40, 8, s, s, 64, 64);

            // 🔴 DAMAGE FLASH (Sadece skin üzerine vuran kırmızı efekt)
            if (damageFlash > 0.1f) {
                int rA = (int)(Math.min(damageFlash, 1.0f) * 180);
                ctx.fill(x + 5, y + 5, x + 5 + s, y + 5 + s, (rA << 24) | 0xFF0000);
            }

            // ✍️ BİLGİLER
            String name = living.getName().getString().toLowerCase();
            ctx.drawTextWithShadow(mc.textRenderer, name, x + 40, y + 5, 0xFFFFFFFF);

            // Şaşırtıcı Detay: Can %25'in altındaysa yazı kırmızı yanıp söner (KRİTİK DURUM)
            int hpColor = (hp / max < 0.25f && (int)(anim * 5) % 2 == 0) ? 0xFFFF0000 : 0xFFAAAAAA;
            String hpInfo = String.format(Locale.US, "hp: %.1f / %.0f", hp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpInfo, x + 40, y + 15, hpColor);

            // ❤️ CAN BARI (Dinamik sarsıntılı)
            int barX = x + 40;
            int barY = y + 27;
            int barW = w - 48;
            int barH = 7;
            
            // Sarsıntı efekti (Hasar alınınca bar titrer)
            int shake = (int)(Math.sin(anim * 20) * (heartBeat * 3));
            
            ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF222222); // Bar arka plan

            int filled = (int)(barW * (Math.max(0, smoothHp) / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(barX + i, barY + shake, barX + i + 1, barY + barH + shake, gradient(anim, i * 0.12f));
            }
            
            // ⚡ KRİTİK VURUŞ GÖSTERGESİ (Ekstra şaşırtıcı özellik)
            if (hp / max < 0.15f) {
                ctx.drawTextWithShadow(mc.textRenderer, "!!! LOW !!!", x + w - 45, y + 5, 0xFFFF5555);
            }
        });
    }
}
