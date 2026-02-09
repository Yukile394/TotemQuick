package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.screen.Screen;

import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHealth = -1;
    private static float hue = 0f;

    private static int hsvToRgb(float h, float s, float v) {
        int rgb = java.awt.Color.HSBtoRGB(h, s, v);
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }

    static {
        HudRenderCallback.EVENT.register(new HudRenderCallback() {
            @Override
            public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player == null || client.world == null) return;
                if (client.currentScreen != null) return;

                HitResult hit = client.crosshairTarget;
                if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
                    smoothHealth = -1;
                    return;
                }

                Entity entity = ((EntityHitResult) hit).getEntity();
                if (!(entity instanceof LivingEntity living)) return;

                float health = living.getHealth();
                float maxHealth = living.getMaxHealth();

                if (smoothHealth < 0) smoothHealth = health;
                smoothHealth += (health - smoothHealth) * 0.12f;

                // RGB renk kaydırma
                hue += 0.002f;
                if (hue > 1f) hue = 0f;
                int rgbColor = hsvToRgb(hue, 0.9f, 1f);

                int x = 10;
                int y = 20;

                int barWidth = 140;
                int barHeight = 12;

                // Panel gölgesi
                context.fill(x - 4, y - 4, x + barWidth + 4, y + barHeight + 28, 0x66000000);
                // Panel arkaplan
                context.fill(x - 2, y - 2, x + barWidth + 2, y + barHeight + 26, 0xAA0F0F0F);

                // Bar arkaplanı
                context.fill(x, y, x + barWidth, y + barHeight, 0xFF222222);

                int filled = (int) (barWidth * (smoothHealth / maxHealth));

                // RGB can barı
                context.fill(x, y, x + filled, y + barHeight, rgbColor);

                String name = living.getName().getString();

                // Locale.US ile -> Arapça rakam sorunu biter
                String hpText = String.format(Locale.US, "%.1f / %.1f ❤", smoothHealth, maxHealth);

                // İsim (üstte)
                context.drawTextWithShadow(client.textRenderer, name, x, y + barHeight + 2, 0xFFFFFFFF);

                // Can yazısı (sağa)
                int hpWidth = client.textRenderer.getWidth(hpText);
                context.drawTextWithShadow(client.textRenderer, hpText, x + barWidth - hpWidth, y + barHeight + 2, 0xFFFFFFFF);
            }
        });
    }
}
