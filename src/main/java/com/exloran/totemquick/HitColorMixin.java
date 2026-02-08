package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class DupeMixins {

    // === ESKİ DUPE KODU DEVRE DIŞI (DOKUNMADAN KALDIRDIK) ===
    // Artık bu mixin sadece Hit Color overlay çizer

    @Inject(method = "render", at = @At("TAIL"))
    private void totemquick$drawCustomHitOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

        if (!config.hitColorEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        // Hasar aldıysa
        if (player.hurtTime > 0) {
            int color = TotemQuickConfig.parseHitColorToRGBA(config.hitColor, config.hitAlpha);

            int w = client.getWindow().getScaledWidth();
            int h = client.getWindow().getScaledHeight();

            // Ekranın tamamına overlay
            context.fill(0, 0, w, h, color);
        }
    }
}
