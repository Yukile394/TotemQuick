package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Elytra gizleme
 */
@Mixin(ElytraFeatureRenderer.class)
public class ClientRenderMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hideElytra(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                            LivingEntity entity, float limbAngle, float limbDistance, float tickDelta,
                            float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {

        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        if (config.elytraGizle) {
            ci.cancel(); // Elytra çizilmesin
        }
    }
}
