package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
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
    private static float starRot = 0;

    private static int gradient(float t, float o) {
        float r = 0.6f + 0.4f * (float) Math.sin(t + o);
        float g = 0.8f;
        float b = 0.2f;
        return 0xFF000000
                | ((int) (r * 255) << 16)
                | ((int) (g * 255) << 8)
                | (int) (b * 255);
    }

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

            TotemQuickConfig cfg = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
            if (!cfg.enabled) return;

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

            smoothHp += (hp - smoothHp) * (hp < smoothHp ? 0.30f : 0.12f);

            anim += 0.04f;
            starRot += cfg.starRotateSpeed;
            damageFlash *= 0.85f;

            // 📐 HUD
            int x = 8;
            int y = 18;
            int w = 150;
            int h = 36;

            ctx.fill(x, y, x + w, y + h, 0xAA000000);

            // 🧑 SKIN FACE
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
            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    living.getName().getString(),
                    x + 26, y + 4,
                    0xFFFFFFFF
            );

            // ❤️ HP text
            String hpText = String.format(Locale.US, "HP %.1f / %.1f", smoothHp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + 26, y + 15, 0xFFCCCCCC);

            // ❤️ BAR
            int barX = x + 26;
            int barY = y + h - 7;
            int barW = w - 32;

            ctx.fill(barX, barY, barX + barW, barY + 4, 0xFF2A2A2A);

            int filled = (int) (barW * (smoothHp / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(
                        barX + i,
                        barY,
                        barX + i + 1,
                        barY + 4,
                        gradient(anim, i * 0.15f)
                );
            }

            // 💥 Hit flash (RENK CONFIG’TEN)
            if (damageFlash > 0.05f) {
                int c = TotemQuickConfig.parseHex(cfg.hitFlashColor, cfg.hitFlashAlpha);
                ctx.fill(x, y, x + w, y + h, c);
            }

            // ✯ HITBOX ORTASI YILDIZ
            int cx = mc.getWindow().getScaledWidth() / 2;
            int cy = mc.getWindow().getScaledHeight() / 2;

            ctx.getMatrices().push();
            ctx.getMatrices().translate(cx, cy, 0);
            ctx.getMatrices().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotation(starRot));
            ctx.getMatrices().scale(cfg.starScale, cfg.starScale, 1f);

            ctx.drawTextWithShadow(
                    mc.textRenderer,
                    "✯",
                    -3, -4,
                    TotemQuickConfig.parseHex(cfg.starColor, 100)
            );

            ctx.getMatrices().pop();
        });
    }
                }
