package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Locale;
import java.util.Random;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHp = -1;
    private static float lastHp = -1;
    private static float anim = 0;
    private static float damageFlash = 0;
    private static float colorSeed = 0;

    private static final Random RANDOM = new Random();

    // ❤️ 1.21 UYUMLU IDENTIFIER
    private static final Identifier HEART =
            Identifier.of("totemquick", "textures/gui/heart.png");

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {

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

            if (smoothHp < 0) {
                smoothHp = hp;
                lastHp = hp;
            }

            // 💥 VURUŞ → RENK PALETİ DEĞİŞİR
            if (hp < lastHp) {
                damageFlash = 1.0f;
                colorSeed = RANDOM.nextFloat() * 10f;
            }
            lastHp = hp;

            smoothHp += (hp - smoothHp) * (hp < smoothHp ? 0.25f : 0.12f);
            anim += tickDelta * 0.35f;
            damageFlash = Math.max(0, damageFlash - 0.05f);

            // 📐 PANEL
            int x = 12;
            int y = 12;
            int w = 150;
            int h = 38;
            int s = 28;

            int shake = (int)(damageFlash * 3);
            ctx.fill(x + shake, y, x + w + shake, y + h, 0xDD000000);

            // ❤️ KALP (SKIN YOK)
            int pulse = (int)(Math.sin(anim * 5) * 2);
            ctx.drawTexture(HEART, x + 6, y + 6 + pulse, 0, 0, s, s, s, s);

            // 🔴 HASAR PARLAMASI
            if (damageFlash > 0) {
                int a = (int)(damageFlash * 160);
                ctx.fill(x + 6, y + 6, x + 6 + s, y + 6 + s,
                        (a << 24) | 0xFF0000);
            }

            // ✍️ YAZILAR
            String name = living.getName().getString().toLowerCase(Locale.ROOT);
            ctx.drawTextWithShadow(mc.textRenderer, name, x + s + 12, y + 5, 0xFFFFFFFF);

            String hpText = String.format(Locale.US, "hp: %.1f / %.0f", hp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + s + 12, y + 15, 0xFFBBBBBB);

            // 🔋 CAN BARİ
            int barX = x + s + 12;
            int barY = y + 26;
            int barW = w - (s + 20);
            int barH = 7;

            ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF151515);

            int filled = (int)(barW * (Math.max(0, smoothHp) / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(barX + i, barY, barX + i + 1, barY + barH,
                        getDynamicColor(anim, i * 0.15f, damageFlash, colorSeed));
            }

            // 🫀 KRİTİK NABIZ
            if (hp / max < 0.3f) {
                float pulseEdge = Math.abs((float)Math.sin(anim * 6));
                int a = (int)(pulseEdge * 120);
                ctx.fill(x, y, x + w, y + 1, (a << 24) | 0xFF0000);
                ctx.fill(x, y + h - 1, x + w, y + h, (a << 24) | 0xFF0000);
            }
        });
    }

    // 🌈 RENK MOTORU
    private static int getDynamicColor(float t, float o, float flash, float seed) {
        float r = 0.5f + 0.5f * (float)Math.sin(t + o + seed);
        float g = 0.5f + 0.5f * (float)Math.sin(t + o + 2 + seed);
        float b = 0.5f + 0.5f * (float)Math.sin(t + o + 4 + seed);

        r = Math.min(1, r + flash * 0.6f);
        g = Math.max(0, g - flash * 0.4f);

        return 0xFF000000
                | ((int)(r * 255) << 16)
                | ((int)(g * 255) << 8)
                | (int)(b * 255);
    }
                }
