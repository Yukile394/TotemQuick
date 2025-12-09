package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.client.gui.ScreenAccessor;

public class TotemQuickServer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            if (client.player == null || screen == null) return;

            int x = screen.width / 2 + 80;
            int y = screen.height / 2 - 100;

            ButtonWidget button = ButtonWidget.builder(Text.literal("Babakral Dupe"), b -> {

                MinecraftClient mc = MinecraftClient.getInstance();

                // SLOT 15 tıklama
                mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        15,
                        1,
                        SlotActionType.QUICK_MOVE,
                        mc.player
                );

                // Chat mesajı
                mc.player.networkHandler.sendChatMessage("/ah sell 70");

                // 600 ms gecikmeli ikinci click
                new java.util.Timer().schedule(new java.util.TimerTask() {
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
                }, 600);

            }).dimensions(x, y, 100, 20).build();


            // addDrawableChild = protected !!!
            // Bu yüzden mixin interface'i üzerinden ekliyoruz.
            ((ScreenAccessor) screen).callAddDrawableChild(button);

        });
    }
}
