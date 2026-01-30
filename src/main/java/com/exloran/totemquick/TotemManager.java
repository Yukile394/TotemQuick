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
                        GLFW.GLFW_KEY_L,
                        "TotemQuick"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            TotemQuickConfig config =
                    AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            // Modu aç/kapat
            while (keyL.wasPressed()) {
                config.enabled = !config.enabled;
                client.player.sendMessage(
                        Text.literal("TotemQuick: " + (config.enabled ? "AÇIK" : "KAPALI")),
                        true
                );
            }

            // TotemQuick aktif ve offhand’de totem yoksa
            if (config.enabled && client.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                logic(client, config);
            }
        });
    }

    private static void logic(MinecraftClient client, TotemQuickConfig config) {
        int slot = -1;

        // Inventoryde totem arama
        for (int i = 0; i < 36; i++) {
            if (client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                slot = i;
                break;
            }
        }

        if (slot != -1) {
            // Hotbar için syncSlot
            int syncSlot = slot < 9 ? slot + 36 : slot;
            client.interactionManager.clickSlot(
                    client.player.currentScreenHandler.syncId,
                    syncSlot,
                    40,
                    SlotActionType.SWAP,
                    client.player
            );
        } else {
            // Totem yok uyarısı
            Formatting renk =
                    Formatting.byName(config.uyarirengi.toUpperCase()) != null
                            ? Formatting.byName(config.uyarirengi.toUpperCase())
                            : Formatting.RED;

            client.player.sendMessage(
                    Text.literal("TOTEM BİTTİ!").formatted(renk),
                    true
            );

            // Rahatlatıcı ses
            if (config.sesliUyari) {
                switch (config.sesSecimi) {
                    case "Pling" -> client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    case "Bell" -> client.player.playSound(SoundEvents.BLOCK_BELL_USE, 1.0f, 1.0f);
                    case "Chime" -> client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                    case "XP" -> client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    case "LevelUp" -> client.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    default -> client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                }
            }
        }
    }
}
