package com.exloran.totemquick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.client.network.ClientPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class DupeMixins {

    @Inject(method = "init", at = @At("TAIL"))
    private void addDupeButton(CallbackInfo ci) {
        Screen screen = (Screen)(Object)this;

        // Sadece envanter ve benzeri handled ekranlarda göster
        if (!(screen instanceof HandledScreen<?>)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Buton pozisyonu: sol tarafta
        int x = 5;
        int y = 5;

        ButtonWidget dupeButton = ButtonWidget.builder(
                Text.literal("Dupe"),
                btn -> onDupePressed()
        ).dimensions(x, y, 60, 20).build();

        // Senin verdiğin accessor ile ekliyoruz
        ((ScreenAccessor) screen).callAddDrawableChild(dupeButton);
    }

    private void onDupePressed() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null) return;

        // Güvenlik: Sadece singleplayer + creative
        if (!client.isInSingleplayer() || !player.getAbilities().creativeMode) {
            player.sendMessage(Text.literal("§cBu buton sadece Singleplayer + Creative için (öğretici demo)."), false);
            return;
        }

        ItemStack mainHand = player.getMainHandStack();

        if (mainHand.isEmpty()) {
            player.sendMessage(Text.literal("§eElinde eşya yok."), false);
            return;
        }

        // Kopyasını oluştur
        ItemStack copy = mainHand.copy();

        // Envantere ekle
        boolean added = player.getInventory().insertStack(copy);

        if (added) {
            player.sendMessage(Text.literal("§aEşya kopyalandı (öğretici demo)."), false);
        } else {
            player.sendMessage(Text.literal("§cEnvanter dolu!"), false);
        }
    }
}
