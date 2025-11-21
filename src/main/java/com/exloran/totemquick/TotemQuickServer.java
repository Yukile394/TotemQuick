package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TotemQuickServer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Ekran açılınca buton ekleme
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            // Buton solda gözükecek
            int x = 10;   // sol taraf sabit
            int y = screen.height / 2 - 10;

            screen.addDrawableChild(
                ButtonWidget.builder(Text.literal("Dupe"), button -> {
                    client.player.sendChatMessage("/dupe");
                })
                .dimensions(x, y, 60, 20)
                .build()
            );
        });
    }
}
