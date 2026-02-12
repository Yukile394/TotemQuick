package com.exloran.totemquick.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.OverlayTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.client.gui.screen.Screen.class)
public abstract class DupeMixins {

    // 🎯 Target texture
    private static final Identifier TARGET =
            Identifier.of("totemquick", "textures/gui/target.png");

    static {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;

            // Crosshair neye bakıyor?
            HitResult hit = mc.crosshairTarget;
            if (!(hit instanceof EntityHitResult ehr)) return;

            Entity e = ehr.getEntity();
            if (!(e instanceof PlayerEntity player)) return;

            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();

            // Player'ın üstü (hitbox üstü)
            double x = player.getX();
            double y = player.getY() + player.getHeight() + 0.2;
            double z = player.getZ();

            // Kameraya göre konum
            double camX = camera.getPos().x;
            double camY = camera.getPos().y;
            double camZ = camera.getPos().z;

            matrices.push();
            matrices.translate(x - camX, y - camY, z - camZ);

            // Kameraya baksın (billboard)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

            // Zaman
            long time = System.currentTimeMillis();

            // Yavaş dönüş
            float angle = (time % 10000L) / 10000f * 360f;
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));

            // Mesafeye göre ölçek
            float distance = (float) mc.player.distanceTo(player);
            float scale = Math.max(0.03f, 0.06f - distance * 0.002f);
            matrices.scale(scale, scale, scale);

            // Pembe ↔ Sarı renk geçişi
            float t = (float) (Math.sin(time / 500.0) * 0.5 + 0.5);
            float r = 1.0f;
            float g = 0.5f + 0.5f * t;
            float b = 0.7f * (1.0f - t);

            VertexConsumerProvider consumers = context.consumers();
            VertexConsumer vc = consumers.getBuffer(RenderLayer.getEntityTranslucent(TARGET));

            Matrix4f mat = matrices.peek().getPositionMatrix();

            float size = 0.5f; // dünya içi boyut

            vc.vertex(mat, -size, -size, 0)
                    .color(r, g, b, 1f)
                    .texture(0, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 0, 1)
                    .endVertex();

            vc.vertex(mat, size, -size, 0)
                    .color(r, g, b, 1f)
                    .texture(1, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 0, 1)
                    .endVertex();

            vc.vertex(mat, size, size, 0)
                    .color(r, g, b, 1f)
                    .texture(1, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 0, 1)
                    .endVertex();

            vc.vertex(mat, -size, size, 0)
                    .color(r, g, b, 1f)
                    .texture(0, 0)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(0xF000F0)
                    .normal(0, 0, 1)
                    .endVertex();

            matrices.pop();
        });
    }
}.
