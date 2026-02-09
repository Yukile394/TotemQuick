package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHp = -1;
    private static float lastHp = -1;
    private static float anim = 0;
    private static float damageFlash = 0;
    private static float targetRot = 0;

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            TotemQuickConfig cfg = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            if (!cfg.enabled) return;
            if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) {
                smoothHp = -1;
                lastHp = -1;
                return;
            }

            Entity ent = ehr.getEntity();
            if (!(ent instanceof LivingEntity living)) return;

            // ================= HP =================
            float hp = living.getHealth();
            float max = living.getMaxHealth();

            if (smoothHp < 0) {
                smoothHp = hp;
                lastHp = hp;
            }

            if (hp < lastHp) damageFlash = 1f;
            lastHp = hp;

            smoothHp += (hp - smoothHp) * (hp < smoothHp ? 0.3f : 0.12f);
            anim += 0.04f;
            damageFlash *= 0.85f;

            // ================= HUD =================
            int x = 8 + cfg.hudOffsetX;
            int y = 18 + cfg.hudOffsetY;
            int w = (int)(150 * cfg.hudScale);
            int h = (int)(36 * cfg.hudScale);

            ctx.fill(x, y, x + w, y + h, 0xAA000000);

            // ================= SKIN FACE (BÜYÜK + SADECE KAFA) =================
            Identifier skin = mc.getEntityRenderDispatcher()
                    .getRenderer(living)
                    .getTexture(living);

            int faceSize = (int)(24 * cfg.hudScale);

            // FACE
            ctx.drawTexture(skin,
                    x + 6, y + 6,
                    8, 8,
                    faceSize, faceSize,
                    64, 64
            );

            // HAT / OVERLAY (şapka katmanı)
            ctx.drawTexture(skin,
                    x + 6, y + 6,
                    40, 8,
                    faceSize, faceSize,
                    64, 64
            );

            // ================= TEXT =================
            String name = living.getName().getString();
            String hpText = String.format(Locale.US, "%.1f / %.1f ❤", smoothHp, max);

            ctx.drawTextWithShadow(mc.textRenderer, name, x + 36, y + 6, 0xFFFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + 36, y + 18, 0xFFDDDDDD);

            // ================= HP BAR =================
            int barX = x + 36;
            int barY = y + h - 6;
            int barW = w - 44;

            ctx.fill(barX, barY, barX + barW, barY + 4, 0xFF222222);

            int filled = (int)(barW * (smoothHp / max));
            int animColor = TotemQuickConfig.parseHitColorToRGBA(cfg.healthAnimColor, 100);

            ctx.fill(barX, barY, barX + filled, barY + 4, animColor);

            if (damageFlash > 0.05f) {
                int a = (int)(damageFlash * 90);
                ctx.fill(x, y, x + w, y + h, (a << 24) | 0x660000);
            }

            // ================= TARGET 🍭 =================
            if (cfg.targetEnabled) {
                Vec3d center = living.getBoundingBox().getCenter();

                targetRot += cfg.targetRotateSpeed;
                if (targetRot > 360) targetRot = 0;

                mc.textRenderer.drawWithShadow(
                        ctx.getMatrices(),
                        cfg.targetSymbol,
                        (float)(center.x),
                        (float)(center.y + living.getHeight() / 2),
                        TotemQuickConfig.parseHitColorToRGBA(cfg.targetColor, cfg.targetAlpha)
                );
            }
        });
    }
                }
