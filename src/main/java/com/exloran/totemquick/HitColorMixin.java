package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntityRenderer.class)
public abstract class HitColorMixin<T extends LivingEntity> {

    @ModifyArgs(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
        )
    )
    private void totemquick$changeColor(Args args) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

        if (!config.hitColorEnabled) return;

        float[] rgb = TotemQuickConfig.parseHexOrNameToRGB(config.hitColor);

        // index: 4=r, 5=g, 6=b, 7=a  (bazı mappinglerde 6-7-8-9 olur, ama ModifyArgs daha güvenli)
        args.set(4, rgb[0]);
        args.set(5, rgb[1]);
        args.set(6, rgb[2]);
        args.set(7, 1.0F); // alpha
    }
}
