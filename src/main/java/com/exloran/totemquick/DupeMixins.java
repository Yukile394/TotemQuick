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
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHp = -1;
    private static float lastHp = -1;
    private static float anim = 0;
    private static float damageFlash = 0;

    // ✯ animasyonu
    private static float starRot = 0f;

    private static int gradient(float t, float o) {
        float r = 0.6f + 0.4f * (float)Math.sin(t + o);
        float g = 0.8f + 0.2f * (float)Math.sin(t + o + 2);
        float b = 0.2f;
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

            float speed = hp < smoothHp ? 0.30f : 0.12f;
            smoothHp += (hp - smoothHp) * speed;

            anim += 0.035f;
            damageFlash *= 0.85f;
            starRot += 0.02f; // ✯ yavaş dönüş

            // 📐 HUD konumu
            int x = 8;
            int y = 18;
            int w = 160;
            int h = 40;

            // 🌫️ Arkaplan
            ctx.fill(x, y, x + w, y + h, 0xAA000000);

            // 🧑 SKIN – SADECE YÜZ (BÜYÜK)
            Identifier skin = mc.getEntityRenderDispatcher()
                    .getRenderer(living)
                    .getTexture(living);

            // Face: (8,8) → (16,16)
            ctx.drawTexture(
                    skin,
                    x + 6, y + 6,
                    8, 8,
                    16, 16,
                    64, 64
            );

            // ✍️ Nick
            String name = living.getName().getString();
            ctx.drawTextWithShadow(mc.textRenderer, name, x + 30, y + 4, 0xFFFFFFFF);

            // ❤️ HP yazısı
            String hpText = String.format(Locale.US, "HP %.1f / %.1f", smoothHp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + 30, y + 16, 0xFFCCCCCC);

            // ❤️ Can barı
            int barX = x + 30;
            int barY = y + h - 8;
            int barW = w - 36;

            ctx.fill(barX, barY, barX + barW, barY + 4, 0xFF2A2A2A);

            int filled = (int)(barW * (smoothHp / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(
                        barX + i,
                        barY,
                        barX + i + 1,
                        barY + 4,
                        gradient(anim, i * 0.15f)
                );
            }

            // 💥 Hasar flash
            if (damageFlash > 0.05f) {
                int a = (int)(damageFlash * 90);
                ctx.fill(x, y, x + w, y + h, (a << 24) | 0x990000);
            }

            // ============================
            // ✯ HITBOX ORTASI HEDEF
            // ============================
            Vec3d pos = living.getPos().add(0, living.getHeight() * 0.5, 0);
            Vec3d screen = mc.gameRenderer.getCamera().getProjection().project(pos);

            if (!Double.isNaN(screen.x)) {
                int cx = (int)(mc.getWindow().getScaledWidth() / 2f);
                int cy = (int)(mc.getWindow().getScaledHeight() / 2f);

                ctx.getMatrices().push();
                ctx.getMatrices().translate(cx, cy, 0);
                ctx.getMatrices().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotation(starRot));
                ctx.getMatrices().scale(1.2f, 1.2f, 1f);

                ctx.drawTextWithShadow(
                        mc.textRenderer,
                        "✯",
                        -3,
                        -4,
                        0xFF00FF66
                );

                ctx.getMatrices().pop();
            }
        });
    }
                }
