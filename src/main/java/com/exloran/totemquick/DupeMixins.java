package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public abstract class DupeMixins {

    private static float smoothHealth = -1;

    // Basit renk geçişi
    private static int getHealthColor(float pct) {
        if (pct > 0.66f) return 0xFF55FF55; // yeşil
        if (pct > 0.33f) return 0xFFFFAA00; // turuncu
        return 0xFFFF5555; // kırmızı
    }

    static {
        // HUD'a çizim ekliyoruz (envanter açıkken çizmez)
        HudRenderCallback.EVENT.register((DrawContext context, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;
            if (client.currentScreen != null) return; // envanter vs açıkken gösterme

            HitResult hit = client.crosshairTarget;
            if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
                smoothHealth = -1;
                return;
            }

            Entity entity = ((EntityHitResult) hit).getEntity();
            if (!(entity instanceof LivingEntity living)) return;

            float health = living.getHealth();
            float maxHealth = living.getMaxHealth();
            float pct = health / maxHealth;

            if (smoothHealth < 0) smoothHealth = health;
            // Yumuşak geçiş
            smoothHealth += (health - smoothHealth) * 0.1f;

            int screenW = context.getScaledWindowWidth();

            // Konum: sol üst - ortaya yakın
            int x = 10;
            int y = 20;

            int barWidth = 120;
            int barHeight = 10;

            // Arkaplan
            context.fill(x - 2, y - 2, x + barWidth + 2, y + barHeight + 12, 0xAA000000);

            // Bar arkaplanı
            context.fill(x, y, x + barWidth, y + barHeight, 0xFF222222);

            int filled = (int) (barWidth * (smoothHealth / maxHealth));
            int color = getHealthColor(smoothHealth / maxHealth);

            // Can barı
            context.fill(x, y, x + filled, y + barHeight, color);

            // İsim + can yazısı
            String name = living.getName().getString();
            String hpText = String.format("%.1f / %.1f ❤", smoothHealth, maxHealth);

            context.drawTextWithShadow(client.textRenderer, Text.literal(name), x, y + 14, 0xFFFFFFFF);
            context.drawTextWithShadow(client.textRenderer, Text.literal(hpText), x + barWidth - client.textRenderer.getWidth(hpText), y + 14, 0xFFFF55FF);
        });
    }
}
