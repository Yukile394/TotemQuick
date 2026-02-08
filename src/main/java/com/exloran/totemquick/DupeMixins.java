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

    private static final ExecutorService DUPE_EXECUTOR = Executors.newFixedThreadPool(2);
    private static boolean DUPE_ENABLED = false;

    // Pembe Renk Teması (Daha temiz bir görünüm için)
    private static String pink(String s) {
        return "§d" + s; // Minecraft Pembe Renk Kodu
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addCustomButtons(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        
        // --- 1. SOL ÜST: MAP KOPYALA BUTONU ---
        // Bu buton tüm ekranlarda (Menü dahil) görünür.
        int topX = 5;
        int topY = 5;
        
        ButtonWidget mapCopyBtn = ButtonWidget.builder(
                Text.literal(pink("Map Kopyala")),
                b -> {
                    // WorldTools indirme tetikleme simülasyonu veya mesajı
                    if(MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.sendMessage(Text.literal("§d[Sistem] §fHarita verileri yakalanıyor..."), false);
                    }
                }
        ).dimensions(topX, topY, 90, 20).build();
        
        ((ScreenAccessor) screen).callAddDrawableChild(mapCopyBtn);

        // --- 2. ENVANTER İÇİ BUTONLAR ---
        // Sadece sandık, envanter vb. açıkken görünür.
        if (!(screen instanceof HandledScreen<?> handled)) return;

        int invX = 8;
        int invY = 30; // Map Kopyala'nın altında başlar
        int w = 110;
        int h = 18;

        // DUPE (YUKILE) BUTONU
        ButtonWidget dupeBtn = ButtonWidget.builder(
                Text.literal(pink("Dupe (Yukile)")),
                b -> {
                    DUPE_ENABLED = !DUPE_ENABLED;
                    runDupeLogic(handled);
                    b.setMessage(Text.literal(DUPE_ENABLED ? "§aÇalışıyor..." : pink("Dupe (Yukile)")));
                }
        ).dimensions(invX, invY, w, h).build();

        // WORLDTOOLS (YUKILE) BUTONU
        ButtonWidget wtBtn = ButtonWidget.builder(
                Text.literal(pink("WorldTools (Yukile)")),
                b -> {
                    // Buraya WorldTools'un GUI'sini açma kodu gelebilir
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("§d[WorldTools] §fDünya yedeği hazırlanıyor!"), false);
                }
        ).dimensions(invX, invY + 22, w, h).build();

        ((ScreenAccessor) screen).callAddDrawableChild(dupeBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(wtBtn);
    }

    // Basitleştirilmiş Hızlı Dupe Mantığı
    private void runDupeLogic(HandledScreen<?> screen) {
        if (!DUPE_ENABLED) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) return;

        DUPE_EXECUTOR.execute(() -> {
            try {
                int syncId = screen.getScreenHandler().syncId;
                // Seri paket gönderimi (Öğretici amaçlı hızlı burst)
                for (int i = 0; i < 20; i++) {
                    client.execute(() -> 
                        client.interactionManager.clickSlot(syncId, 36, 0, SlotActionType.QUICK_MOVE, client.player)
                    );
                    Thread.sleep(10); 
                }
                DUPE_ENABLED = false; // İşlem bitince durdur
            } catch (Exception ignored) {}
        });
    }
}

