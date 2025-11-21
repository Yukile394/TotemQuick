package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TotemQuickServer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            // Buton solda gözüksün
            int x = 10;
            int y = screen.height / 2 - 10;

            screen.addDrawableChild(
                ButtonWidget.builder(Text.literal("Dupe"), button -> {

                    // /dupe çalıştır
                    if (client.player != null) {
                        client.player.networkHandler.sendChatCommand("dupe");
                    }

                }).dimensions(x, y, 60, 20).build()
            );
        });
    }
}
