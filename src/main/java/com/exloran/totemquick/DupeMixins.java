package com.exloran.totemquick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class DupeMixins {

    @Inject(method = "init", at = @At("TAIL"))
    private void addUltraDupeButton(CallbackInfo ci) {
        Screen screen = (Screen)(Object)this;
        if (!(screen instanceof HandledScreen<?>)) return;

        // Butonu ekranın tam sol ortasına yerleştiriyoruz (Dikkat çekici renk)
        ButtonWidget dupeBtn = ButtonWidget.builder(
                Text.literal("§4§l[!] §fULTRA DUPE §4§l[!]"), 
                btn -> startHighProbabilityDupe((HandledScreen<?>) screen)
        ).dimensions(5, screen.height / 2 - 10, 100, 20).build();

        ((ScreenAccessor) screen).callAddDrawableChild(dupeBtn);
    }

    private void startHighProbabilityDupe(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager == null || client.player == null) return;

        client.player.sendMessage(Text.literal("§e[Sistem] §6Yüksek ihtimalli Dupe motoru çalışıyor..."), false);

        // AYNI ANDA ÇALIŞAN PACKET THREAD'LERİ
        // Bu yapı sunucuya o kadar çok paket gönderir ki sunucu hangisini önce işleyeceğini şaşırır.
        new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    int syncId = screen.getScreenHandler().syncId;
                    
                    // Slot 36 (Hotbar 1. slot) - Sürekli 'Quick Move' gönderir
                    client.execute(() -> {
                        client.interactionManager.clickSlot(syncId, 36, 0, SlotActionType.QUICK_MOVE, client.player);
                    });
                    
                    // Milisaniyelik gecikme ile sunucunun paket sırasını bozmaya çalışıyoruz
                    Thread.sleep(1); 
                }
                client.player.sendMessage(Text.literal("§a[!] İşlem tamam. Envanteri/Sandığı kontrol et!"), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
