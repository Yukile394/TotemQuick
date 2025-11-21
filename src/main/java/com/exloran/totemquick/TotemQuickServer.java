package com.exloran.totemquick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class TotemQuickServer implements ClientModInitializer {
    
    private static KeyBinding totemKey;
    
    @Override
    public void onInitializeClient() {
        // Totem takası için bir tuş atayalım (ÖRNEK: "K" tuşu)
        totemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.totemquick.swap", // çeviri anahtarı
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K, // K tuşu
            "category.totemquick.main" // kategori
        ));
        
        // Her tick'te tuş kontrolü
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (totemKey.wasPressed()) {
                // Totem takas kodu buraya gelecek
                if (client.player != null) {
                    // Örnek: Oyuncuya mesaj gönder
                    client.player.sendMessage(net.minecraft.text.Text.literal("Totem takası aktif!"), false);
                }
            }
        });
    }
}
