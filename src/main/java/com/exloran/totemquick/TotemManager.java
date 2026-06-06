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

    // "Totem bitti" spam'ini önlemek için
    private static int warnCooldown = 0;

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

            if (warnCooldown > 0) warnCooldown--;

            TotemQuickConfig config =
                AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            while (keyL.wasPressed()) {
                config.enabled = !config.enabled;
                String durum = config.enabled ? "§a§lAÇIK" : "§c§lKAPALI";
                client.player.sendMessage(
                    Text.literal("§6✨ TotemQuick: " + durum), true
                );
                if (config.sesliUyari) {
                    client.player.playSound(
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                        1.0f, config.enabled ? 1.2f : 0.8f
                    );
                }
            }
        });
    }

    /**
     * Totem patlama packet'i geldiğinde Mixin'den çağrılır.
     * Bu noktada sunucu zaten "totem kullanıldı" dedi,
     * offhand'i boşaltacak — biz hemen yenisini koyuyoruz.
     */
    public static void onTotemPop(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        TotemQuickConfig config =
            AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        if (!config.enabled) return;

        var inv = client.player.getInventory();

        int slot = -1;
        for (int i = 0; i < inv.main.size(); i++) {
            if (inv.main.get(i).isOf(Items.TOTEM_OF_UNDYING)) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            if (warnCooldown == 0) {
                Formatting renk = Formatting.byName(config.uyarirengi.toLowerCase());
                if (renk == null) renk = Formatting.RED;
                client.player.sendMessage(
                    Text.literal("⚠ TOTEM BİTTİ!").formatted(Formatting.BOLD, renk), true
                );
                if (config.sesliUyari) {
                    client.player.playSound(SoundEvents.BLOCK_BELL_RESONATE, 1.0f, 0.8f);
                }
                warnCooldown = 20;
            }
            return;
        }

        int syncSlot = (slot < 9) ? slot + 36 : slot;

        client.interactionManager.clickSlot(
            client.player.currentScreenHandler.syncId,
            syncSlot,
            40,
            SlotActionType.SWAP,
            client.player
        );

        if (config.sesliUyari) {
            client.player.playSound(
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value(), 1.0f, 1.5f
            );
        }
    }
}
