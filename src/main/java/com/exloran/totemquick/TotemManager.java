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

    // Cooldown: arka arkaya swap denemelerini engeller (flicker fix)
    private static int cooldownTicks = 0;
    private static final int COOLDOWN = 10; // 10 tick = 0.5 saniye

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

            // Cooldown say
            if (cooldownTicks > 0) {
                cooldownTicks--;
            }

            TotemQuickConfig config =
                AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

            // Toggle tuşu
            while (keyL.wasPressed()) {
                config.enabled = !config.enabled;

                String durum = config.enabled ? "§a§lAÇIK" : "§c§lKAPALI";
                client.player.sendMessage(
                    Text.literal("§6✨ TotemQuick: " + durum),
                    true
                );

                if (config.sesliUyari) {
                    client.player.playSound(
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                        1.0f,
                        config.enabled ? 1.2f : 0.8f
                    );
                }
            }

            // Totem logic: sadece cooldown bittiyse ve offhand'de totem yoksa çalış
            if (config.enabled
                    && cooldownTicks == 0
                    && !client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                logic(client, config);
            }
        });
    }

    private static void logic(MinecraftClient client, TotemQuickConfig config) {
        var inv = client.player.getInventory();

        // Envanterde totem bul (hotbar dahil tüm main)
        int slot = -1;
        for (int i = 0; i < inv.main.size(); i++) {
            if (inv.main.get(i).isOf(Items.TOTEM_OF_UNDYING)) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            // Totem yok uyarısı
            Formatting renk = Formatting.byName(config.uyarirengi.toLowerCase());
            if (renk == null) renk = Formatting.RED;

            client.player.sendMessage(
                Text.literal("⚠ TOTEM BİTTİ!").formatted(Formatting.BOLD, renk),
                true
            );

            if (config.sesliUyari) {
                client.player.playSound(
                    SoundEvents.BLOCK_BELL_RESONATE,
                    1.0f,
                    0.8f
                );
            }
            return;
        }

        /*
         * Minecraft inventory syncSlot düzeni (PlayerScreenHandler):
         *   Slot  0       = craft output
         *   Slot  1-4     = craft grid
         *   Slot  5-8     = armor
         *   Slot  9-35    = main inventory (üst 3 sıra)  → syncSlot = slot (player.getInventory() index eşleşir)
         *   Slot  36-44   = hotbar (sıra 0-8)            → syncSlot = slot + 36
         *   Slot  45      = offhand
         *
         * player.getInventory().main index:
         *   0-8   = hotbar
         *   9-35  = main inventory
         */
        int syncSlot;
        if (slot < 9) {
            // Hotbar slotları
            syncSlot = slot + 36;
        } else {
            // Main inventory slotları (9-35)
            syncSlot = slot;
        }

        // action=40 → F tuşu (offhand swap) — sunucu tarafında da çalışır
        client.interactionManager.clickSlot(
            client.player.currentScreenHandler.syncId,
            syncSlot,
            40,
            SlotActionType.SWAP,
            client.player
        );

        // Cooldown başlat (flicker engelle)
        cooldownTicks = COOLDOWN;

        if (config.sesliUyari) {
            client.player.playSound(
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                1.0f,
                1.5f
            );
        }
    }
}
