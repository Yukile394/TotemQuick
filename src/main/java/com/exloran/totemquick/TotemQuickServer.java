package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class TotemQuickServer implements ClientModInitializer {

    private KeyBinding totemKey;

    @Override
    public void onInitializeClient() {

        // K tuşu
        totemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.totemquick.swap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.totemquick.main"
        ));

        // Her tick kontrol
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (totemKey.wasPressed()) {
                if (client.player != null) {
                    swapTotem();
                }
            }
        });
    }

    private void swapTotem() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;

        var inv = client.player.getInventory();

        // Zaten offhand'de totem varsa hiçbir şey yapma
        if (client.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            client.player.sendMessage(Text.literal("Zaten offhand'de totem var."), false);
            return;
        }

        // Hotbarda totem ara
        int totemSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }

        if (totemSlot == -1) {
            client.player.sendMessage(Text.literal("Hotbar'da totem yok!"), false);
            return;
        }

        // Hotbar’daki totem
        ItemStack totem = inv.getStack(totemSlot);

        // Offhand’deki mevcut item
        ItemStack offhand = client.player.getOffHandStack();

        // Yer değiştir
        inv.setStack(totemSlot, offhand);
        client.player.getInventory().offHand.set(0, totem);

        client.player.sendMessage(Text.literal("Totem Offhand'e takıldı!"), false);
    }
}
