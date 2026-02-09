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

    private static int gradient(float t, float o) {
        float r = 0.5f + 0.5f * (float)Math.sin(t + o);
        float g = 0.6f + 0.4f * (float)Math.sin(t + o + 2);
        float b = 0.2f + 0.2f * (float)Math.sin(t + o + 4);
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

            float speed = hp < smoothHp ? 0.28f : 0.12f;
            smoothHp += (hp - smoothHp) * speed;

            anim += 0.035f;
            damageFlash *= 0.85f;

            int x = 8;
            int y = 18;
            int w = 140;
            int h = 34;

            // 🌫️ Gölge
            ctx.fill(x, y, x + w, y + h, 0x88000000);
            ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xFF0F0F0F);

            // 🧑 ENTITY HEAD (skin)
            Identifier skin = mc.getEntityRenderDispatcher()
                    .getRenderer(living)
                    .getTexture(living);

            ctx.drawTexture(
                    skin,
                    x + 4, y + 5,
                    8, 8,
                    8, 8,
                    32, 32
            );

            // ✍️ Nick
            String name = living.getName().getString();
            ctx.drawTextWithShadow(mc.textRenderer, name, x + 26, y + 4, 0xFFFFFFFF);

            // ❤️ Can yazısı
            String hpText = String.format(Locale.US, "HP %.1f (%.1f)", smoothHp, max);
            ctx.drawTextWithShadow(mc.textRenderer, hpText, x + 26, y + 14, 0xFFCCCCCC);

            // ❤️ Can barı
            int barX = x + 26;
            int barY = y + h - 8;
            int barW = w - 32;

            ctx.fill(barX, barY, barX + barW, barY + 4, 0xFF2A2A2A);

            int filled = (int)(barW * (smoothHp / max));
            for (int i = 0; i < filled; i++) {
                ctx.fill(
                        barX + i,
                        barY,
                        barX + i + 1,
                        barY + 4,
                        gradient(anim, i * 0.12f)
                );
            }

            // 💥 Hasar flash
            if (damageFlash > 0.05f) {
                int a = (int)(damageFlash * 100);
                ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, (a << 24) | 0x990000);
            }
        });
    }
}
