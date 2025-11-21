package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class TotemQuickServer implements ClientModInitializer {

    private static KeyBinding totemKey;

    @Override
    public void onInitializeClient() {

        // -----------------------------
        //  K TUŞU — TOTEM TİCARETİ
        // -----------------------------

        totemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.totemquick.swap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.totemquick.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (totemKey.wasPressed()) {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Totem takası aktif!"), false);
                }
            }
        });


        // -----------------------------
        //  DUPE BUTONU — ENVANTER SOL KENAR
        // -----------------------------
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            // Sadece envanter ekranında gözüksün
            if (!(screen instanceof net.minecraft.client.gui.screen.ingame.InventoryScreen)) return;

            int x = 10; // Sol taraf
            int y = screen.height / 2 - 10;

            screen.addDrawableChild(
                ButtonWidget.builder(Text.literal("DUPE"), button -> {

                    if (client.player != null) {
                        // /dupe komutu gönder
                        client.player.networkHandler.sendChatCommand("dupe");
                    }

                }).dimensions(x, y, 60, 20).build()
            );
        });
    }
}
