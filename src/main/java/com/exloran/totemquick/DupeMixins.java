package com.exloran.totemquick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class DupeMixins {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTargetHealth(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) hit;
        if (!(entityHit.getEntity() instanceof LivingEntity living)) return;

        float health = living.getHealth();
        float maxHealth = living.getMaxHealth();

        String name = living.getDisplayName().getString();
        String hpText = String.format("§c❤ %.1f §7/ §c%.1f", health, maxHealth);

        int screenWidth = client.getWindow().getScaledWidth();

        int x = screenWidth / 2;
        int y = 20; // Ekranın üst tarafı

        // İsim
        context.drawCenteredTextWithShadow(
                client.textRenderer,
                Text.literal("§d" + name),
                x,
                y,
                0xFFFFFF
        );

        // Can yazısı
        context.drawCenteredTextWithShadow(
                client.textRenderer,
                Text.literal(hpText),
                x,
                y + 10,
                0xFFFFFF
        );

        // Basit can barı
        int barWidth = 100;
        int barHeight = 6;

        int barX = x - barWidth / 2;
        int barY = y + 24;

        float ratio = Math.max(0.0f, Math.min(1.0f, health / maxHealth));
        int filled = (int) (barWidth * ratio);

        // Arka plan
        context.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0x90000000);
        // Kırmızı can
        context.fill(barX, barY, barX + filled, barY + barHeight, 0xFFFF5555);
    }
}
