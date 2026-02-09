package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHp = -1;
    private static float lastHp = -1;
    private static float anim = 0;

    private static float damageFlash = 0;
    private static float criticalFlash = 0;
    private static float shake = 0;

    private static int hpGradient(float t, float o) {
        float r = 0.3f;
        float g = 0.8f + 0.2f * (float) Math.sin(t + o);
        float b = 0.3f;
        return 0xFF000000
                | ((int)(r * 255) << 16)
                | ((int)(g * 255) << 8)
                | (int)(b * 255);
    }

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

            TotemQuickConfig cfg =
                    AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
            if (!cfg.enabled) return;

            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) {
                smoothHp = -1;
                lastHp = -1;
                return;
            }

            Entity e = ehr.getEntity();
            if (!(e instanceof LivingEntity living)) return;

            float hp = living.getHealth();
            float max = living.getMaxHealth();

            if (smoothHp < 0) {
                smoothHp = hp;
                lastHp = hp;
            }

            float diff = lastHp - hp;

            // 💥 KRİTİK ALGILAMA
            if (cfg.criticalEnabled && diff >= cfg.criticalDamageThreshold) {
                criticalFlash = 1f;
                shake = cfg.criticalShakePower;
            } else if (hp < lastHp) {
                damageFlash = 1f;
            }

            lastHp = hp;

            smoothHp += (hp - smoothHp) * cfg.hpSmoothSpeed;

            anim += 0.04f;
            damageFlash *= 0.85f;
            criticalFlash *= 0.8f;
            shake *= 0.85f;

            int shakeX = (int)((Math.random() - 0.5) * shake);
            int shakeY = (int)((Math.random() - 0.5) * shake);

            float scale = cfg.hudScale;

            int x = (int)((8 + shakeX) * scale);
            int y = (int)((16 + shakeY) * scale);
            int w = (int)(170 * scale);
            int h = (int)(44 * scale);

            // 🖤 Arka plan
            ctx.fill(x, y, x + w, y + h, 0xAA000000);

            // 🧑 DOĞRU SKIN YÜZÜ (8,8 → 16,16) ve BÜYÜTÜLMÜŞ
            Identifier skin = mc.getEntityRenderDispatcher()
                    .getRenderer(living)
                    .getTexture(living);

            int face = cfg.faceSize;

            ctx.drawTexture(
                    skin,
                    x + 8, y + 6,
                    8, 8,           // DOĞRU YÜZ UV
                    face, face,     // GERÇEK BÜYÜKLÜK
                    64, 64
            );

            // ✍️ Nick
            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    living.getName().getString(),
                    x + face + 16,
                    y + 6,
                    0xFFFFFFFF
            );

            // ❤️ HP yazısı
            String hpText = String.format(Locale.US, "%.1f / %.1f", smoothHp, max);
            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    hpText,
                    x + face + 16,
                    y + 18,
                    0xFFE0E0E0
            );

            // ❤️ HP BAR
            int barX = x + face + 16;
            int barY = y + h - 8;
            int barW = w - face - 24;

            ctx.fill(barX, barY, barX + barW, barY + 4, 0xFF222222);

            int filled = (int)(barW * (smoothHp / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(
                        barX + i,
                        barY,
                        barX + i + 1,
                        barY + 4,
                        hpGradient(anim, i * 0.15f)
                );
            }

            // 💥 Normal hit flash
            if (damageFlash > 0.05f) {
                int a = (int)(damageFlash * 80);
                ctx.fill(x, y, x + w, y + h, (a << 24) | 0x99FFCC00);
            }

            // ☠️ KRİTİK FLASH
            if (criticalFlash > 0.05f) {
                int a = (int)(criticalFlash * 140);
                ctx.fill(
                        x, y, x + w, y + h,
                        (a << 24) | (cfg.criticalFlashColor & 0x00FFFFFF)
                );
            }
        });
    }
                }
