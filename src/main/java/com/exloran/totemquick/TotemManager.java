package com.exloran.totemquick;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class TotemManager {

    public static KeyBinding keyL;

    private static int cooldownTicks = 0;
    private static final int SWAP_COOLDOWN   = 10;  // normal swap sonrası: 0.5s
    private static final int TOTEM_POP_DELAY = 40;  // totem patladıktan sonra: 2s bekle

    // Önceki tick'te offhand'de totem var mıydı? (patlama tespiti için)
    private static boolean hadTotemLastTick = false;

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

            if (cooldownTicks > 0) cooldownTicks--;

            TotemQuickConfig config =
                AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            // Toggle tuşu
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

            // Offhand'deki mevcut durum
            ItemStack offhand = client.player.getOffHandStack();
            boolean hasTotemNow = offhand.isOf(Items.TOTEM_OF_UNDYING);

            // PATLAMA TESPİTİ:
            // Geçen tick totem vardı, bu tick yok → totem patladı
            // Ekstra kontrol: oyuncunun canı 1 kalp civarında veya
            // absorpsiyon efekti var (totem efekti) — ama en basit yol
            // sadece "vardı → yok" geçişini yakalamak.
            if (hadTotemLastTick && !hasTotemNow) {
                // Totem patladı, sunucunun işlemesi için uzun cooldown koy
                cooldownTicks = TOTEM_POP_DELAY;
            }

            hadTotemLastTick = hasTotemNow;

            // Swap logic: cooldown bitti + offhand'de totem yok
            if (config.enabled && cooldownTicks == 0 && !hasTotemNow) {
                logic(client, config);
            }
        });
    }

    private static void logic(MinecraftClient client, TotemQuickConfig config) {
        var inv = client.player.getInventory();

        int slot = -1;
        for (int i = 0; i < inv.main.size(); i++) {
            if (inv.main.get(i).isOf(Items.TOTEM_OF_UNDYING)) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            Formatting renk = Formatting.byName(config.uyarirengi.toLowerCase());
            if (renk == null) renk = Formatting.RED;
            client.player.sendMessage(
                Text.literal("⚠ TOTEM BİTTİ!").formatted(Formatting.BOLD, renk), true
            );
            if (config.sesliUyari) {
                client.player.playSound(SoundEvents.BLOCK_BELL_RESONATE, 1.0f, 0.8f);
            }
            return;
        }

        // syncSlot: hotbar(0-8) → +36, main(9-35) → direkt
        int syncSlot = (slot < 9) ? slot + 36 : slot;

        client.interactionManager.clickSlot(
            client.player.currentScreenHandler.syncId,
            syncSlot,
            40,                   // F tuşu = offhand swap
            SlotActionType.SWAP,
            client.player
        );

        cooldownTicks = SWAP_COOLDOWN;

        if (config.sesliUyari) {
            client.player.playSound(
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value(), 1.0f, 1.5f
            );
        }
    }
}
