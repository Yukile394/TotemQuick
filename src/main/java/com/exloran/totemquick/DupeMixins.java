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
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class DupeMixins {

    // 🎯 Target texture
    private static final Identifier TARGET =
            Identifier.of("totemquick", "textures/gui/target.png");

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.currentScreen != null) return;

            TotemQuickConfig cfg =
                    AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            // Crosshair neye bakıyor?
            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) return;

            Entity e = ehr.getEntity();
            if (!(e instanceof PlayerEntity)) return; // sadece player

            // Ekran merkezi (hitbox merkezi gibi davranır)
            int sw = mc.getWindow().getScaledWidth();
            int sh = mc.getWindow().getScaledHeight();
            int cx = sw / 2;
            int cy = sh / 2;

            // Zaman tabanlı animasyon
            long time = System.currentTimeMillis();

            // Sürekli ve yumuşak dönüş
            float angle = (time % 8000L) / 8000f * 360f;

            // Hafif renk geçişi (istersen kaldırabiliriz)
            float t = (float) (Math.sin(time / 400.0) * 0.5 + 0.5);
            float r = 1.0f;
            float g = 0.6f + 0.4f * t;
            float b = 0.6f * (1.0f - t);

            int size = 64; // 🔥 Daha büyük ve “gerçekçi” his

            ctx.getMatrices().push();

            // Merkeze git
            ctx.getMatrices().translate(cx, cy, 0);
            // Döndür
            ctx.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
            // Texture merkezden dönsün diye geri al
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

            // Rengi sıfırla
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            ctx.getMatrices().pop();
        });
    }
}
