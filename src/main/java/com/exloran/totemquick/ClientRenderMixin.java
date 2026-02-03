package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.entity.LivingEntityRenderer;
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

        int rgb = parseHex(config.hitColorHex);
        return ((rgb >> 16) & 0xFF) / 255.0f;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"), index = 7)
    private float changeGreen(float green) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        if (!config.hitColorEnabled) return green;

        int rgb = parseHex(config.hitColorHex);
        return ((rgb >> 8) & 0xFF) / 255.0f;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"), index = 8)
    private float changeBlue(float blue) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        if (!config.hitColorEnabled) return blue;

        int rgb = parseHex(config.hitColorHex);
        return (rgb & 0xFF) / 255.0f;
    }

    private int parseHex(String hex) {
        try {
            return Integer.parseInt(hex, 16);
        } catch (Exception e) {
            return 0xFF0000; // varsayılan kırmızı
        }
    }
}
