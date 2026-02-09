package com.exloran.totemquick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class DupeMixins {

    private static float hue = 0.0f;

    private int rgbColor() {
        // Hue 0..1 arası döner
        hue += 0.002f;
        if (hue > 1.0f) hue = 0.0f;

        int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
        return 0xFF000000 | (rgb & 0x00FFFFFF); // alpha ekle
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTargetHealth(DrawContext context, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) hit;
        if (!(entityHit.getEntity() instanceof LivingEntity living)) return;

        float health = living.getHealth();
        float maxHealth = living.getMaxHealth();

        String name = living.getDisplayName().getString();
        String hpText = String.format("❤ %.1f / %.1f", health, maxHealth);

        // Sol-üst, biraz ortaya yakın
        int x = 80;
        int y = 20;

        int color = rgbColor();

        // İsim (RGB animasyonlu)
        context.drawTextWithShadow(
                client.textRenderer,
                Text.literal(name),
                x,
                y,
                color
        );

        // Can yazısı
        context.drawTextWithShadow(
                client.textRenderer,
                Text.literal(hpText),
                x,
                y + 10,
                color
        );

        // Can barı
        int barWidth = 100;
        int barHeight = 6;

        int barX = x;
        int barY = y + 24;

        float ratio = Math.max(0.0f, Math.min(1.0f, health / maxHealth));
        int filled = (int) (barWidth * ratio);

        // Arka plan
        context.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0x90000000);
        // RGB animasyonlu bar
        context.fill(barX, barY, barX + filled, barY + barHeight, color);
    }
}
