package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class TotemQuickServer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Envanter açıldığında buton ekle
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            if (screen == null || client.player == null) return;

            // Buton yerleşimi
            int x = screen.width / 2 + 80;
            int y = screen.height / 2 - 100;

            screen.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Babakral Dupe"), button -> {

                        MinecraftClient mc = MinecraftClient.getInstance();

                        // 1. SLOT tıklama
                        mc.interactionManager.clickSlot(
                                mc.player.currentScreenHandler.syncId,
                                15, // fotoğraftaki slot
                                1,
                                SlotActionType.QUICK_MOVE,
                                mc.player
                        );

                        // Chat gönder
                        mc.player.networkHandler.sendChatMessage("/ah sell 70");

                        // 2. SLOT tıklamasını 600 ms geciktirerek çalıştır
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        MinecraftClient.getInstance().execute(() -> {

                                            MinecraftClient mc2 = MinecraftClient.getInstance();

                                            mc2.interactionManager.clickSlot(
                                                    mc2.player.currentScreenHandler.syncId,
                                                    10,
                                                    0,
                                                    SlotActionType.QUICK_MOVE,
                                                    mc2.player
                                            );

                                        });
                                    }
                                },
                                600
                        );

                    }).dimensions(x, y, 95, 20).build()
            );

        });
    }
}
