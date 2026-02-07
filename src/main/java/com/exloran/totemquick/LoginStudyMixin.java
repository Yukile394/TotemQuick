package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget; // dış yapı bozulmasın diye duruyor
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Screen.class, InGameHud.class})
public abstract class LoginStudyMixin {

    // Dış yapı bozulmasın diye alanlar duruyor
    private TextFieldWidget passwordField;
    private boolean isAuthorized = false;

    // Mevcut init inject'in (boş kalabilir)
    @Inject(method = "init", at = @At("TAIL"))
    private void addSecurityLayer(CallbackInfo ci) {
        // UI eklemiyoruz, sadece config güvenliği
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        if (config.hitColor == null || config.hitColor.isEmpty()) {
            config.hitColor = "red";
            AutoConfig.getConfigHolder(TotemQuickConfig.class).save();
        }
    }

    // === HIT COLOR ===
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
