package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Quaternionf;

public abstract class DupeMixins {

    // 🎯 Target icon
    private static final Identifier TARGET =
            Identifier.of("totemquick", "textures/gui/target.png");

    static {
        HudRenderCallback.EVENT.register((ctx, tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.currentScreen != null) return;

            TotemQuickConfig cfg =
                    AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            // Crosshair neye bakıyor?
            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) return;

            Entity e = ehr.getEntity();
            if (!(e instanceof PlayerEntity)) return; // sadece player

            // Ekranın ortası
            int sw = mc.getWindow().getScaledWidth();
            int sh = mc.getWindow().getScaledHeight();
            int cx = sw / 2;
            int cy = sh / 2;

            long time = System.currentTimeMillis();

            // Dönüş
            float angleDeg = (time % 10000L) / 10000f * 360f;
            float angleRad = (float) Math.toRadians(angleDeg);

            ctx.getMatrices().push();

            // Merkeze taşı
            ctx.getMatrices().translate(cx, cy, 0);

            // 1.21 uyumlu dönüş
            ctx.getMatrices().multiply(new Quaternionf().rotateZ(angleRad));

            int size = 64;

            // Merkezden dönmesi için geri al
            ctx.getMatrices().translate(-size / 2f, -size / 2f, 0);

            // Çiz (RENKLE OYNAMIYORUZ)
            ctx.drawTexture(
                    TARGET,
                    0, 0,
                    0, 0,
                    size, size,
                    size, size
            );

            ctx.getMatrices().pop();
        });
    }
}
