package com.exloran.totemquick;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class TotemManager {

    public static KeyBinding keyL;

    private static int warnCooldown = 0;

    public static void init() {

        /* KeyBinding */
        keyL = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "TotemQuick Aç/Kapat",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                "TotemQuick"
            )
        );

        /* CLIENT TICK */
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            TotemQuickConfig config =
                AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            /* Aç / Kapat */
            while (keyL.wasPressed()) {
                config.enabled = !config.enabled;

                client.player.sendMessage(
                    Text.literal("✨ TotemQuick: " + (config.enabled ? "AÇIK" : "KAPALI"))
                        .formatted(Formatting.GOLD),
                    true
                );

                if (config.sesliUyari) {
                    client.player.playSound(
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP.value(),
                        1.0f,
                        1.0f
                    );
                }
            }

            if (warnCooldown > 0) warnCooldown--;

            if (config.enabled &&
                !client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                logic(client, config);
            }
        });

        /* FAKE HITBOX RENDER (SADECE GÖRSEL) */
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            TotemQuickConfig config =
                AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            if (!config.enabled) return;

            float[] rgb = TotemQuickConfig.parseHitboxColor(config.hitboxRengi);

            Vec3d cam = context.camera().getPos();
            Box box = client.player.getBoundingBox().offset(-cam.x, -cam.y, -cam.z);

            VertexConsumer vc =
                context.consumers().getBuffer(RenderLayer.getLines());

            WorldRenderer.drawBox(
                context.matrixStack(),
                vc,
                box,
                rgb[0], rgb[1], rgb[2],
                1.0f
            );
        });
    }

    private static void logic(MinecraftClient client, TotemQuickConfig config) {
        int slot = -1;

        /* Envanterde totem ara */
        for (int i = 0; i < client.player.getInventory().main.size(); i++) {
            if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                slot = i;
                break;
            }
        }

        /* Totem bulunduysa offhand */
        if (slot != -1) {
            int syncSlot = slot < 9 ? slot + 36 : slot;

            client.interactionManager.clickSlot(
                client.player.currentScreenHandler.syncId,
                syncSlot,
                40,
                SlotActionType.SWAP,
                client.player
            );

            if (config.sesliUyari) {
                client.player.playSound(
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP.value(),
                    0.8f,
                    1.1f
                );
            }
        }
        /* Totem yok */
        else {
            if (warnCooldown > 0) return;
            warnCooldown = 40;

            Formatting renk = TotemQuickConfig.parseColor(config.uyarirengi);

            client.player.sendMessage(
                Text.literal("⚠ TOTEM BİTTİ!").formatted(renk),
                true
            );

            if (config.sesliUyari) {
                client.player.playSound(
                    SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(),
                    0.9f,
                    0.6f
                );
            }
        }
    }
}
