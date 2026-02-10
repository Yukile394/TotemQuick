package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class DupeMixins {

    static {
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

            // 📐 ORTA BOY – FOTOĞRAF ORANI KORUNDU
            int x = 10;
            int y = 20;
            int w = 140;
            int h = 34;

            // 🧱 DIŞ ÇERÇEVE (fotoğraftaki sade stil)
            int borderColor = 0xFFFFFFFF; // beyaz çerçeve
            int bgColor = 0x66000000;     // hafif şeffaf iç (boş görünüm)

            // Arka plan (boş ama şeffaf)
            ctx.fill(x, y, x + w, y + h, bgColor);

            // Üst
            ctx.fill(x, y, x + w, y + 1, borderColor);
            // Alt
            ctx.fill(x, y + h - 1, x + w, y + h, borderColor);
            // Sol
            ctx.fill(x, y, x + 1, y + h, borderColor);
            // Sağ
            ctx.fill(x + w - 1, y, x + w, y + h, borderColor);
        });
    }
}
