package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntityRenderer.class)
public class HitColorMixin {

    @ModifyArg(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
        ),
        index = 6
    )
    private float changeRed(float red) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        if (!config.hitColorEnabled) return red;

        Formatting f = TotemQuickConfig.parseColor(config.hitColor);
        return colorToRGB(f)[0];
    }

    @ModifyArg(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
        ),
        index = 7
    )
    private float changeGreen(float green) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        if (!config.hitColorEnabled) return green;

        Formatting f = TotemQuickConfig.parseColor(config.hitColor);
        return colorToRGB(f)[1];
    }

    @ModifyArg(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
        ),
        index = 8
    )
    private float changeBlue(float blue) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        if (!config.hitColorEnabled) return blue;

        Formatting f = TotemQuickConfig.parseColor(config.hitColor);
        return colorToRGB(f)[2];
    }

    private float[] colorToRGB(Formatting f) {
        // Basit renk eşlemesi
        return switch (f) {
            case RED -> new float[]{1f, 0f, 0f};
            case GREEN -> new float[]{0f, 1f, 0f};
            case BLUE -> new float[]{0f, 0f, 1f};
            case YELLOW -> new float[]{1f, 1f, 0f};
            case AQUA -> new float[]{0f, 1f, 1f};
            case LIGHT_PURPLE -> new float[]{1f, 0f, 1f};
            case WHITE -> new float[]{1f, 1f, 1f};
            default -> new float[]{1f, 0f, 0f}; // varsayılan kırmızı
        };
    }
}
