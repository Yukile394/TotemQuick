package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class LoginStudyMixin {

    // Hasar alınca çizilen kırmızı overlay rengini değiştirir (1.21 uyumlu)
    @ModifyArg(
            method = "render",
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
