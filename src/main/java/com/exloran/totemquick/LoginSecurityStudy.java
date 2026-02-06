package com.exloran.totemquick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class LoginStudyMixin {
    private TextFieldWidget passwordField;
    private boolean isAuthorized = false;

    @Inject(method = "init", at = @At("TAIL"))
    private void addSecurityLayer(CallbackInfo ci) {
        Screen screen = (Screen)(Object)this;
        // Sadece ana menüde veya belirli bir ekranda çıksın
        if (!screen.getTitle().getString().contains("Inventory") && !isAuthorized) {
            
            // Şifre Giriş Kutusu
            this.passwordField = new TextFieldWidget(
                MinecraftClient.getInstance().textRenderer, 
                screen.width / 2 - 50, screen.height / 2 - 40, 
                100, 20, Text.literal("Sifre")
            );
            
            // Giriş Butonu (Auth Check)
            ButtonWidget loginBtn = ButtonWidget.builder(
                Text.literal("§6Sistemi Aktif Et"), 
                btn -> {
                    if (passwordField.getText().equals("Öğretici2026")) {
                        isAuthorized = true;
                        MinecraftClient.getInstance().player.sendMessage(Text.literal("§aErişim Onaylandı!"), false);
                    } else {
                        MinecraftClient.getInstance().player.sendMessage(Text.literal("§cHatalı Şifre! Deneme kaydedildi."), false);
                    }
                }
            ).dimensions(screen.width / 2 - 50, screen.height / 2 - 10, 100, 20).build();

            ((ScreenAccessor) screen).callAddDrawableChild(loginBtn);
        }
    }
}

