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
    // Static değişkenler sınıfın en başında olmalı
    private static int soundCooldown = 0;
    private static final int THREE_MINUTES_IN_TICKS = 3 * 60 * 20; 

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

            // Zamanlayıcıyı azalt
            if (soundCooldown > 0) {
                soundCooldown--;
            }

            try {
                TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

                while (keyL.wasPressed()) {
                    config.enabled = !config.enabled;
                    client.player.sendMessage(Text.literal("TotemQuick: " + (config.enabled ? "AÇIK" : "KAPALI")), true);
                }

                if (config.enabled && client.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                    executeTotemLogic(client, config);
                }
            } catch (Exception e) {
                // Config yüklenirken hata oluşursa build düşmesin diye basit bir try-catch
            }
        });
    }

    private static void executeTotemLogic(MinecraftClient client, TotemQuickConfig config) {
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            if (client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                slot = i;
                break;
            }
        }

        if (slot != -1) {
            int syncSlot = slot < 9 ? slot + 36 : slot;
            // Bazı sürümlerde clickSlot parametre sayısı farklıdır, burayı kontrol et
            client.interactionManager.clickSlot(
                    client.player.currentScreenHandler.syncId,
                    syncSlot,
                    40,
                    SlotActionType.SWAP,
                    client.player
            );
        } else {
            handleWarning(client, config);
        }
    }

    private static void handleWarning(MinecraftClient client, TotemQuickConfig config) {
        Formatting renk = Formatting.RED;
        try {
            if (config.uyarirengi != null) {
                renk = Formatting.byName(config.uyarirengi);
            }
        } catch (Exception e) {
            renk = Formatting.RED;
        }

        client.player.sendMessage(Text.literal("TOTEM BİTTİ!").formatted(renk), true);

        if (config.sesliUyari && soundCooldown <= 0) {
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            soundCooldown = THREE_MINUTES_IN_TICKS;
        }
    }
}
