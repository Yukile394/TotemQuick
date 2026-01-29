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
    private static int alertCooldown = 0; // Sürekli mesaj atıp ekranı kirletmemesi için

    public static void init() {
        // L Tuşu Tanımlama
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "TotemQuick Aç/Kapat",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "TotemQuick"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // L Tuşuna basıldığında
            while (toggleKey.wasPressed()) {
                isEnabled = !isEnabled;
                Formatting color = isEnabled ? Formatting.GREEN : Formatting.RED;
                client.player.sendMessage(Text.literal("TotemQuick: " + (isEnabled ? "AKTİF" : "KAPALI"))
                        .formatted(color), true);
            }

            if (!isEnabled) return;

            // Ana Mantık
            executeTotemLogic(client);
        });
    }

    private static void executeTotemLogic(MinecraftClient client) {
        var player = client.player;
        
        // Sol el doluysa (totem varsa) dur
        if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        // Envanterde totem ara
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                slot = i;
                break;
            }
        }

        if (slot != -1) {
            // Totem bulundu! Sol ele (40. slot) takasla
            int syncSlot = slot < 9 ? slot + 36 : slot;
            client.interactionManager.clickSlot(player.currentScreenHandler.syncId, syncSlot, 40, SlotActionType.SWAP, player);
        } else {
            // Totem bittiyse UYARI VER
            if (alertCooldown <= 0) {
                player.sendMessage(Text.literal("⚠ DİKKAT: TOTEM BİTTİ!").formatted(Formatting.BOLD, Formatting.RED), false);
                // Sesli uyarı ekleyelim (Daha fazla şey!)
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.5F); 
                alertCooldown = 100; // 5 saniye sonra tekrar uyar
            } else {
                alertCooldown--;
            }
        }
    }
}
