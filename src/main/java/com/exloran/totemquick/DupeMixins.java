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
import org.spongepowered.asm.mixin.Mixin;

import java.awt.*;
import java.util.Locale;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHealth = -1f;
    private static float anim = 0f;

    private static int rgb(float r, float g, float b) {
        return 0xFF000000 | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
    }

    static {
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;
            if (client.currentScreen != null) return;

            HitResult hit = client.crosshairTarget;
            if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
                smoothHealth = -1;
                return;
            }

            Entity e = ((EntityHitResult) hit).getEntity();
            if (!(e instanceof LivingEntity living)) return;

            float health = living.getHealth();
            float max = living.getMaxHealth();

            if (smoothHealth < 0) smoothHealth = health;
            smoothHealth += (health - smoothHealth) * 0.10f;

            anim += 0.03f;

            // 🔥 Premium renk animasyonu (sine wave)
            float r = 0.6f + 0.4f * (float)Math.sin(anim);
            float g = 0.4f + 0.4f * (float)Math.sin(anim + 2);
            float b = 0.6f + 0.4f * (float)Math.sin(anim + 4);
            int healthColor = rgb(r, g, b);

            int size = 110; // kare boyut (orta)
            int x = 10;
            int y = 20;

            // 🌑 Gölge
            context.fill(x - 3, y - 3, x + size + 3, y + size + 3, 0x66000000);

            // 🖤 Ana panel
            context.fill(x, y, x + size, y + size, 0xFF0E0E0E);

            // 🔲 İç çerçeve efekti
            context.fill(x + 2, y + 2, x + size - 2, y + size - 2, 0xFF161616);

            // ❤️ Can barı (alt kısım)
            int barHeight = 10;
            int barY = y + size - barHeight - 6;

            context.fill(x + 6, barY, x + size - 6, barY + barHeight, 0xFF2A2A2A);

            int filled = (int)((size - 12) * (smoothHealth / max));
            context.fill(x + 6, barY, x + 6 + filled, barY + barHeight, healthColor);

            // 🧑 Entity adı (üst orta)
            String name = living.getName().getString();
            int nameW = client.textRenderer.getWidth(name);
            context.drawTextWithShadow(
                    client.textRenderer,
                    name,
                    x + size / 2 - nameW / 2,
                    y + 8,
                    0xFFFFFFFF
            );

            // 💖 Can yazısı (TAM ORTA)
            String hpText = String.format(Locale.US, "%.0f / %.0f ❤", smoothHealth, max);
            int hpW = client.textRenderer.getWidth(hpText);
            context.drawTextWithShadow(
                    client.textRenderer,
                    hpText,
                    x + size / 2 - hpW / 2,
                    y + size / 2 - 4,
                    0xFFFFFFFF
            );
        });
    }
}
