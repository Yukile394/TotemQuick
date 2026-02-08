package com.exloran.totemquick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.Screen.*;

@Mixin(Screen.class)
public abstract class HitColorMixins {

    @Inject(method = "init", at = @At("TAIL"))
    private void addMapCopyButton(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen screen = (Screen) (Object) this; // Mixin cast hack

        int buttonWidth = 100;
        int buttonHeight = 20;
        int x = screen.width / 2 - buttonWidth / 2;
        int y = screen.height - 50;

        screen.addDrawableChild(new ButtonWidget(
                x, y, buttonWidth, buttonHeight,
                Text.literal("Map Kopyala"),
                button -> {
                    if (client.player != null && client.player.getMainHandStack().isEmpty() == false) {
                        client.player.getInventory().insertStack(client.player.getMainHandStack().copy());
                        client.player.sendMessage(Text.literal("Harita kopyalandı!"), true);
                    }
                },
                ButtonWidget.DEFAULT_NARRATION_SUPPLIER
        ));
    }
}
