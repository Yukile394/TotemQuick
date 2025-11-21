package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.slot.SlotActionType;

public class DupeButtonMod implements ClientModInitializer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static boolean spam = false;

    @Override
    public void onInitializeClient() {

        // Envanter ekranı açılınca buton ekle
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof net.minecraft.client.gui.screen.ingame.InventoryScreen) {

                screen.addDrawableChild(ButtonWidget.builder(
                        // Butonun yazısı
                        net.minecraft.text.Text.of("DUPE"),

                        // Tıklanınca yapılacak
                        (button) -> {
                            spam = !spam; // Aç/Kapat
                        }
                )
                        // Konum -> Sol tarafa koyuyoruz
                        .position( screen.x - 40, screen.y + 20 )
                        .size(35, 20)
                        .build()
                );
            }
        });

        // Tick’te spam çalıştır
        ScreenEvents.afterTick(screen -> {
            if (spam) clickSlotSpam();
        });
    }


    // Gerçek slot tıklatma
    private void clickSlotSpam() {
        if (mc == null || mc.interactionManager == null || mc.player == null) return;

        ClientPlayerEntity player = mc.player;

        int syncId = player.currentScreenHandler.syncId;

        int slotId = 36; // Hotbar slot 0
        int button = 1; // Sağ tık
        SlotActionType action = SlotActionType.PICKUP;

        mc.interactionManager.clickSlot(syncId, slotId, button, action, player);
    }
}
