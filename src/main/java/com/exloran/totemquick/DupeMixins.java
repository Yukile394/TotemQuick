package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class DupeMixins {

    // 🎯 Target icon (emoji gibi düşün)
    private static final Identifier TARGET =
            Identifier.of("totemquick", "textures/gui/target.png");

    static {
        HudRenderCallback.EVENT.register((ctx, tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.currentScreen != null) return;

            TotemQuickConfig cfg =
                    AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            // İstersen config’e bağlayabilirsin, şimdilik açık varsayalım
            // if (!cfg.keyboardHudEnabled) return;

            // Crosshair neye bakıyor?
            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) return;

            Entity e = ehr.getEntity();
            if (!(e instanceof PlayerEntity)) return; // sadece player

            // Ekranın ortası = baktığın nokta (hitbox ortası gibi davranır)
            int sw = mc.getWindow().getScaledWidth();
            int sh = mc.getWindow().getScaledHeight();
            int cx = sw / 2;
            int cy = sh / 2;

            // Zaman tabanlı animasyon
            long time = System.currentTimeMillis();

            // Yavaş dönüş
            float angle = (time % 10000L) / 10000f * 360f;

            // Pembe ↔ Sarı renk geçişi
            float t = (float) (Math.sin(time / 500.0) * 0.5 + 0.5);
            float r = 1.0f;
            float g = 0.5f + 0.5f * t; // sarıya doğru gider
            float b = 0.7f * (1.0f - t); // pembeden sarıya geçiş hissi

            ctx.getMatrices().push();

            // Merkeze taşı
            ctx.getMatrices().translate(cx, cy, 0);
            // Döndür
            ctx.getMatrices().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(angle));
            // Geri al (ikon merkezden dönsün)
            int size = 32;
            ctx.getMatrices().translate(-size / 2f, -size / 2f, 0);

            // Renk uygula
            RenderSystem.setShaderColor(r, g, b, 1.0f);

            // Çiz
            ctx.drawTexture(
                    TARGET,
                    0, 0,
                    0, 0,
                    size, size,
                    size, size
            );

            // Rengi sıfırla (başka HUD’ları bozmasın)
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            ctx.getMatrices().pop();
        });
    }
}
