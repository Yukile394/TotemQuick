package com.exloran.totemquick;

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
    public static KeyBinding toggleKey;
    public static boolean isEnabled = true;
    private static int alertCooldown = 0;

    // Config simülasyonu (Gerçek config dosyasından da okunabilir)
    public static Formatting warningColor = Formatting.RED; 

    public static void init() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "TotemQuick: Aç/Kapat",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "TotemQuick"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (toggleKey.wasPressed()) {
                isEnabled = !isEnabled;
                Formatting statusColor = isEnabled ? Formatting.GREEN : Formatting.RED;
                client.player.sendMessage(Text.literal("TotemQuick: " + (isEnabled ? "AKTİF" : "KAPALI"))
                        .formatted(statusColor), true);
            }

            if (isEnabled) {
                runAutoTotem(client);
            }
        });
    }

    private static void runAutoTotem(MinecraftClient client) {
        var player = client.player;
        if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        int totemSlot = -1;
        // Envanteri tara
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }

        if (totemSlot != -1) {
            // Totemi bulduk, otomatik olarak sol ele çek (Multiplayer uyumlu paket gönderimi)
            int syncSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
            client.interactionManager.clickSlot(player.currentScreenHandler.syncId, syncSlot, 40, SlotActionType.SWAP, player);
        } else {
            // Totem bittiyse uyarı ver
            if (alertCooldown <= 0) {
                // Burada config'den gelen rengi (warningColor) kullanıyoruz
                player.sendMessage(Text.literal("⚠ ENVANTERDE TOTEM KALMADI!")
                        .formatted(Formatting.BOLD, warningColor), false);
                
                // Ses efekti (Daha fazla özellik)
                player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.5F, 1.0F);
                alertCooldown = 80; // 4 saniye bekle
            } else {
                alertCooldown--;
            }
        }
    }
}
