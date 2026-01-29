package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        if (client.player == null) return;

        var inv = client.player.getInventory();

        // Envanterde totem ara (main inventory)
        int totemSlot = -1;
        for (int i = 0; i < inv.main.size(); i++) {
            ItemStack s = inv.main.get(i);
            if (!s.isEmpty() && s.getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }

        if (totemSlot == -1) {
            // action bar (true) mesajı
            client.player.sendMessage(Text.literal("Envanterinde Totem yok!"), true);
            return;
        }

        ItemStack totemStack = inv.main.get(totemSlot);
        ItemStack offhandStack = inv.offHand.get(0);

        if (offhandStack.isEmpty()) {
            // Offhand boş -> totemi oraya koy
            inv.offHand.set(0, totemStack.copy());
            inv.main.set(totemSlot, ItemStack.EMPTY);
            client.player.sendMessage(Text.literal("Totem offhand'e kondu."), false);
        } else {
            // Offhand dolu -> swap
            inv.main.set(totemSlot, offhandStack.copy());
            inv.offHand.set(0, totemStack.copy());
            client.player.sendMessage(Text.literal("Totem ile offhand takas edildi."), false);
        }
    }
}
