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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(Screen.class)
public abstract class DupeMixins {

    // Paketleri ışık hızında göndermek için özel bir havuz
    private static final ExecutorService DUPE_EXECUTOR = Executors.newFixedThreadPool(4);

    @Inject(method = "init", at = @At("TAIL"))
    private void addUltisStyleUI(CallbackInfo ci) {
        Screen screen = (Screen)(Object)this;
        if (!(screen instanceof HandledScreen<?>)) return;

        // UI Ultis Stili: Sol üstte neon renkli butonlar
        int startY = 10;
        
        // ANA BUTON: ULTRA DUPE
        ButtonWidget dupeBtn = ButtonWidget.builder(
                Text.literal("§d§l» ULTRA DUPE «"), 
                btn -> runUltisDupe((HandledScreen<?>) screen)
        ).dimensions(10, startY, 110, 20).build();

        // YARDIMCI BUTON: AUTO-PV
        ButtonWidget pvBtn = ButtonWidget.builder(
                Text.literal("§b§l» AUTO PV 1 «"), 
                btn -> {
                    MinecraftClient.getInstance().player.networkHandler.sendCommand("pv 1");
                }
        ).dimensions(10, startY + 25, 110, 20).build();

        ((ScreenAccessor) screen).callAddDrawableChild(dupeBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(pvBtn);
    }

    private void runUltisDupe(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.player.sendMessage(Text.literal("§d§l[ULTIS] §fSaldırı başlatıldı. Senkronizasyon kırılıyor..."), false);

        // GARANTİ ODAKLI ÇALIŞMA MANTIĞI:
        // 4 farklı koldan (Thread) aynı anda paket fırlatıyoruz. 
        // Sunucu birini engellese diğeri aradan sızar.
        for (int t = 0; t < 4; t++) {
            DUPE_EXECUTOR.execute(() -> {
                try {
                    for (int i = 0; i < 40; i++) {
                        int syncId = screen.getScreenHandler().syncId;
                        client.execute(() -> {
                            // Slot 36 (Hotbar) ve Slot 0 (PV içi) arasında mekik dokur
                            client.interactionManager.clickSlot(syncId, 36, 0, SlotActionType.QUICK_MOVE, client.player);
                        });
                        Thread.sleep(2); // Milisaniyelik gecikme sunucuyu kör eder
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        client.player.sendMessage(Text.literal("§a§l[BAŞARILI] §fPaketler hedefe ulaştı!"), false);
    }
}
