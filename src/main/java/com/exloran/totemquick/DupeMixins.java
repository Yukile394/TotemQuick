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

    private static float smoothHp = -1f;
    private static float lastHp = -1f;
    private static float anim = 0f;
    private static float damageFlash = 0f;

    private static int gradient(float t, float o) {
        float r = 0.75f + 0.25f * (float)Math.sin(t + o);
        float g = 0.75f + 0.25f * (float)Math.sin(t + o + 2);
        float b = 0.25f + 0.25f * (float)Math.sin(t + o + 4);
        return 0xFF000000 | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
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

            float speed = hp < smoothHp ? 0.32f : 0.14f;
            smoothHp += (hp - smoothHp) * speed;

            anim += 0.04f;
            damageFlash *= 0.85f;

            // 📐 KÜÇÜK – YATAY – YOL GİBİ
            int x = 8;
            int y = 18;
            int w = 132;
            int h = 30;

            // 🌫️ Arka plan (oval hissi)
            ctx.fill(x, y, x + w, y + h, 0xAA000000);
            ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xFF111111);

            // 🧑 SKIN YÜZÜ – BÜYÜK (SOL)
            Identifier skin = mc.getEntityRenderDispatcher()
                    .getRenderer(living)
                    .getTexture(living);

            // Face: (8,8) → (16,16)
            ctx.drawTexture(
                    skin,
                    x + 5, y + 5,
                    8, 8,
                    16, 16,
                    64, 64
            );

            // ✍️ Nick (SAĞ ÜST)
            String name = living.getName().getString();
            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    name,
                    x + 28,
                    y + 4,
                    0xFFFFFFFF
            );

            // ❤️ HP yazısı (nick altı)
            String hpText = String.format(Locale.US, "%.0f / %.0f", smoothHp, max);
            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    hpText,
                    x + 28,
                    y + 14,
                    0xFFCCCCCC
            );

            // ❤️ CAN BAR (ALTA YAYIK – YOL GİBİ)
            int barX = x + 28;
            int barY = y + h - 6;
            int barW = w - 34;

            ctx.fill(barX, barY, barX + barW, barY + 3, 0xFF2A2A2A);

            int filled = (int)(barW * (smoothHp / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(
                        barX + i,
                        barY,
                        barX + i + 1,
                        barY + 3,
                        gradient(anim, i * 0.18f)
                );
            }

            // 💥 Hasar flash (çok hafif)
            if (damageFlash > 0.05f) {
                int a = (int)(damageFlash * 70);
                ctx.fill(x, y, x + w, y + h, (a << 24) | 0x880000);
            }
        });
    }
                }
