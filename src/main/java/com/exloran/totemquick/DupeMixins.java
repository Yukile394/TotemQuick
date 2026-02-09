package com.exloran.totemquick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class DupeMixins {

    private static float hue = 0.0f;

    private int getRgbColor() {
        hue += 0.002f;
        if (hue > 1.0f) hue = 0.0f;

        float h = hue * 6.0f;
        int i = (int) h;
        float f = h - i;
        float q = 1.0f - f;

        float r = 0, g = 0, b = 0;

        switch (i % 6) {
            case 0 -> { r = 1; g = f; b = 0; }
            case 1 -> { r = q; g = 1; b = 0; }
            case 2 -> { r = 0; g = 1; b = f; }
            case 3 -> { r = 0; g = q; b = 1; }
            case 4 -> { r = f; g = 0; b = 1; }
            case 5 -> { r = 1; g = 0; b = q; }
        }

        int ri = (int)(r * 255);
        int gi = (int)(g * 255);
        int bi = (int)(b * 255);

        return 0xFF000000 | (ri << 16) | (gi << 8) | bi;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTargetHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;

        // GUI açıksa gösterme (envanter, esc vs)
        if (client.currentScreen != null) return;

        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult ehr = (EntityHitResult) hit;
        if (!(ehr.getEntity() instanceof LivingEntity living)) return;

        float health = living.getHealth();
        float maxHealth = living.getMaxHealth();

        String name = living.getDisplayName().getString();

        int x = 80;
        int y = 20;

        int color = getRgbColor();

        // İsim
        context.drawTextWithShadow(
                client.textRenderer,
                Text.literal(name),
                x,
                y,
                color
        );

        // HP text
        String hpText = String.format("❤ %.1f / %.1f", health, maxHealth);
        context.drawTextWithShadow(
                client.textRenderer,
                Text.literal(hpText),
                x,
                y + 10,
                0xFFFFFFFF
        );

        // Bar
        int barWidth = 100;
        int barHeight = 6;
        int barX = x;
        int barY = y + 24;

        float ratio = Math.max(0.0f, Math.min(1.0f, health / maxHealth));
        int filled = (int) (barWidth * ratio);

        // Arkaplan
        context.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0x90000000);
        // RGB dolu kısım
        context.fill(barX, barY, barX + filled, barY + barHeight, color);
    }
}
