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
    private static float shine = 0;

    // 🌟 Altın / Sarı premium gradient
    private static int goldGradient(float t, float o) {
        float r = 0.85f + 0.15f * (float)Math.sin(t + o);
        float g = 0.70f + 0.25f * (float)Math.sin(t + o + 2.0);
        float b = 0.15f + 0.10f * (float)Math.sin(t + o + 4.0);

        int ir = Math.min(255, Math.max(0, (int)(r * 255)));
        int ig = Math.min(255, Math.max(0, (int)(g * 255)));
        int ib = Math.min(255, Math.max(0, (int)(b * 255)));

        return 0xFF000000 | (ir << 16) | (ig << 8) | ib;
    }

    // 🟡 Yuvarlatılmış bar çizimi (köşeler döngü gibi)
    private static void drawRoundedBar(DrawContext ctx, int x, int y, int w, int h, float fill, float time) {
        int radius = h / 2; // orta kalınlık, kapsül şekli

        // Arka plan (koyu)
        for (int i = 0; i < h; i++) {
            int yy = y + i;
            int dx = Math.abs(i - h / 2);
            int cut = (int)Math.sqrt(radius * radius - dx * dx);

            ctx.fill(x + radius - cut, yy, x + w - (radius - cut), yy + 1, 0xFF1E1E1E);
        }

        int filled = (int)(w * fill);
        if (filled <= 0) return;

        // Ön plan (altın animasyonlu)
        for (int i = 0; i < h; i++) {
            int yy = y + i;
            int dx = Math.abs(i - h / 2);
            int cut = (int)Math.sqrt(radius * radius - dx * dx);

            int start = x + radius - cut;
            int end = Math.min(x + filled, x + w - (radius - cut));

            for (int px = start; px < end; px++) {
                float sweep = (float)Math.sin((px * 0.15f) + time * 3f) * 0.5f + 0.5f;
                int col = goldGradient(time * 1.2f, px * 0.08f + sweep);
                ctx.fill(px, yy, px + 1, yy + 1, col);
            }
        }
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

            float speed = hp < smoothHp ? 0.30f : 0.12f;
            smoothHp += (hp - smoothHp) * speed;

            anim += 0.035f;
            shine += 0.05f;
            damageFlash *= 0.85f;

            // 📐 Dış yapı AYNI
            int x = 8;
            int y = 18;
            int w = 150;
            int h = 36;

            // 🌫️ Arka plan
            ctx.fill(x, y, x + w, y + h, 0xAA000000);

            // 🧑 Yüz (8x8)
            Identifier skin = mc.getEntityRenderDispatcher()
                    .getRenderer(living)
                    .getTexture(living);

            ctx.drawTexture(
                    skin,
                    x + 6, y + 6,
                    8, 8,
                    8, 8,
                    64, 64
            );

            // ✍️ Nick
            String name = living.getName().getString();
            ctx.drawTextWithShadow(mc.textRenderer, name, x + 26, y + 4, 0xFFFFE08A);

            // ❤️ HP yazısı
            String hpText = String.format(Locale.US, "HP %.1f / %.1f", smoothHp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + 26, y + 15, 0xFFFFD070);

            // ❤️ Yuvarlak (kapsül) can barı
            int barX = x + 26;
            int barY = y + h - 10;
            int barW = w - 32;
            int barH = 7; // orta kalınlık

            float fill = Math.max(0f, Math.min(1f, smoothHp / max));
            drawRoundedBar(ctx, barX, barY, barW, barH, fill, anim);

            // 💥 Hasar alınca gold flash
            if (damageFlash > 0.05f) {
                int a = (int)(damageFlash * 80);
                ctx.fill(x, y, x + w, y + h, (a << 24) | 0xFFD08000);
            }
        });
    }
}
