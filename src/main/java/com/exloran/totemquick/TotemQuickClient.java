package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class TotemQuickClient implements ClientModInitializer {

    private static KeyBinding totemKey;

    @Override
    public void onInitializeClient() {
        totemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.totemquick.swap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.totemquick"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (totemKey.wasPressed()) {
                handleSwap(client);
            }
        });
    }

    private void handleSwap(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        var inv = client.player.getInventory();

        // Önce offhand'i kontrol et — zaten totem varsa işlem yok
        if (inv.offHand.get(0).isOf(Items.TOTEM_OF_UNDYING)) {
            client.player.sendMessage(
                Text.literal("§e✔ Offhand'de zaten totem var."),
                true
            );
            return;
        }

        // Main envanterde totem ara
        int totemSlot = -1;
        for (int i = 0; i < inv.main.size(); i++) {
            if (inv.main.get(i).isOf(Items.TOTEM_OF_UNDYING)) {
                totemSlot = i;
                break;
            }
        }

        if (totemSlot == -1) {
            client.player.sendMessage(
                Text.literal("§c§l⚠ Envanterinde Totem yok!"),
                true
            );
            return;
        }

        /*
         * syncSlot hesabı:
         *   Hotbar (main[0-8])        → syncSlot = slot + 36
         *   Main inventory (main[9-35]) → syncSlot = slot
         */
        int syncSlot = (totemSlot < 9) ? totemSlot + 36 : totemSlot;

        // clickSlot ile sunucuya paket gönder (gerçek swap)
        client.interactionManager.clickSlot(
            client.player.currentScreenHandler.syncId,
            syncSlot,
            40, // action = F (offhand swap)
            SlotActionType.SWAP,
            client.player
        );

        client.player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1.0f, 1.5f);
        client.player.sendMessage(
            Text.literal("§a§l✔ Totem offhand'e alındı!"),
            true
        );
    }
}
