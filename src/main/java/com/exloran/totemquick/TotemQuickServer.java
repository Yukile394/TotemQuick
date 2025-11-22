package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class TotemQuickServer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Inventory ekranı açıldığında buton ekle
        ScreenMouseEvents.afterInit((screen, scaledWidth, scaledHeight) -> {
            if (screen.getClass().getSimpleName().equals("InventoryScreen")) {
                // Sol üst köşe konumu: x=10, y=10, boyut: 80x20
                ButtonWidget totemButton = new ButtonWidget(
                        10, 10, 80, 20,
                        Text.literal("Totem Ekle"),
                        button -> addTotemToOffhand()
                );
                screen.addDrawableChild(totemButton);
            }
        });
    }

    private void addTotemToOffhand() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        var inv = client.player.getInventory();

        // Offhand zaten totem ise uyar
        if (client.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            client.player.sendMessage(Text.literal("Zaten offhand'de totem var."), false);
            return;
        }

        // Hotbar'da totem ara
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

        // Totemi offhand’e koy, hotbardaki yerini boşalt
        ItemStack totem = inv.getStack(totemSlot);
        ItemStack offhand = client.player.getOffHandStack();

        inv.setStack(totemSlot, offhand);
        client.player.getInventory().offHand.set(0, totem);

        client.player.sendMessage(Text.literal("Totem Offhand'e takıldı!"), false);
    }
}
