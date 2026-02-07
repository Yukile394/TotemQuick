package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class HitColorMixin {

    // Hasar alınca çizilen kırmızı overlay rengini değiştiriyoruz
    @ModifyArg(
            method = "renderVignette",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawableHelper;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"
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
