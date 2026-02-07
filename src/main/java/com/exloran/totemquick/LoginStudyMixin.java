package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class LoginStudyMixin {

    // Hasar alınca çıkan kırmızı vignette rengini config'e göre değiştirir
    @ModifyArg(
            method = "renderVignette",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
            ),
            index = 4
    )
    private int totemquick$changeHitColor(int originalColor) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

        if (!config.hitColorEnabled) {
            return originalColor;
        }

        return TotemQuickConfig.parseHitColorToRGBA(config.hitColor, config.hitAlpha);
    }
}
