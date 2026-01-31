package com.exloran.totemquick;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class TotemManager {

    public static KeyBinding keyL;

    public static void init() {
        keyL = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "TotemQuick Aç/Kapat",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_T,
                "TotemQuick"
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.interactionManager == null) return;

            TotemQuickConfig config =
                AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            while (keyL.wasPressed()) {
                config.enabled = !config.enabled;

                client.player.sendMessage(
                    Text.literal("✨ TotemQuick: " + (config.enabled ? "AÇIK" : "KAPALI")),
                    true
                );

                if (config.sesliUyari) {
                    client.player.playSound(
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                        1.0f,
                        1.0f
                    );
                }
            }

            if (config.enabled &&
                !client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                logic(client, config);
            }
        });
    }

    private static void logic(MinecraftClient client, TotemQuickConfig config) {
        int slot = -1;

        for (int i = 0; i < client.player.getInventory().main.size(); i++) {
            if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                slot = i;
                break;
            }
        }

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
                    SoundEvents.BLOCK_BELL_USE,
                    1.0f,
                    1.2f
                );
            }
        } else {
            Formatting renk =
                Formatting.byName(config.uyarirengi.toLowerCase()) != null
                    ? Formatting.byName(config.uyarirengi.toLowerCase())
                    : Formatting.RED;

            client.player.sendMessage(
                Text.literal("TOTEM BİTTİ!").formatted(renk),
                true
            );

            if (config.sesliUyari) {
                client.player.playSound(
                    SoundEvents.BLOCK_BELL_RESONATE,
                    1.0f,
                    0.8f
                );
            }
        }
    }
}
