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
    private void addAutoDupeButton(CallbackInfo ci) {
        Screen screen = (Screen)(Object)this;
        
        // Sadece ana envanter açıkken bu özel "Kombo" butonu çıksın
        if (!(screen instanceof HandledScreen<?>)) return;

        ButtonWidget comboBtn = ButtonWidget.builder(
                Text.literal("§6§l[AUTO-PV DUPE]"), 
                btn -> executeAutoCombo()
        ).dimensions(10, 85, 120, 20).build();

        ((ScreenAccessor) screen).callAddDrawableChild(comboBtn);
    }

    private void executeAutoCombo() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) return;

        // 1. ADIM: Sunucuya komutu gönder
        client.player.sendMessage(Text.literal("§e[Sistem] §fKomut gönderiliyor: /pv 1"), false);
        client.player.networkHandler.sendCommand("pv 1");

        // 2. ADIM: Arka planda bir Thread açarak sandığın açılmasını bekle ve paketleri gönder
        new Thread(() -> {
            try {
                // Sunucunun sandığı açması için 500ms bekle (Laglı sunucularda 1000ms yapabilirsin)
                Thread.sleep(500); 

                client.execute(() -> {
                    Screen currentScreen = client.currentScreen;
                    if (currentScreen instanceof HandledScreen<?> handledScreen) {
                        client.player.sendMessage(Text.literal("§b[Sistem] §fSandık algılandı! Paket fırtınası başlıyor..."), false);
                        
                        for (int i = 0; i < 80; i++) {
                            // Slot 36 (Hotbar'daki ilk eşya)
                            client.interactionManager.clickSlot(
                                handledScreen.getScreenHandler().syncId, 
                                36, 0, SlotActionType.QUICK_MOVE, client.player
                            );
                        }
                        client.player.sendMessage(Text.literal("§a§l[!] §fKombo tamamlandı!"), false);
                    } else {
                        client.player.sendMessage(Text.literal("§c[Hata] §7Sandık zamanında açılmadı."), false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
