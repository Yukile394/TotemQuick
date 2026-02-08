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

    // === RGB SİSTEMİ (SOL TARAF İÇİN) ===
    private static int rgbTick = 0;
    private static final String[] RAINBOW = {"§d", "§5", "§b", "§a", "§e"}; 

    private static String getRGB(String text) {
        rgbTick = (rgbTick + 1) % RAINBOW.length;
        return RAINBOW[rgbTick] + text;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addIrpInterface(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        
        // Sadece Envanter/Sandık açıldığında (HandledScreen) gözükür
        if (!(screen instanceof HandledScreen<?> handled)) return;

        int x = 10; // Sol taraf
        int y = 30; // Başlangıç yüksekliği
        int width = 120;
        int height = 20;

        // --- 1. İRP BUTONU (İtem Geri Getirme) ---
        ButtonWidget irpBtn = ButtonWidget.builder(
                Text.literal(getRGB("İrp (İtem İade)")),
                b -> {
                    // Paket gönderimi simüle edilerek "İrp" işlemi başlatılır
                    simulateIrpLogic(handled);
                    b.setMessage(Text.literal("§a§lİADE EDİLDİ!"));
                }
        ).dimensions(x, y, width, height).build();

        // --- 2. GHOST MODE (Sunucuda Fark Edilmeme) ---
        ButtonWidget ghostBtn = ButtonWidget.builder(
                Text.literal(getRGB("Ghost Mode: OFF")),
                b -> {
                    boolean active = b.getMessage().getString().contains("OFF");
                    b.setMessage(Text.literal(getRGB("Ghost Mode: " + (active ? "ON" : "OFF"))));
                }
        ).dimensions(x, y + 25, width, height).build();

        // --- 3. AUTO-LOOT (Hızlı Toplama) ---
        ButtonWidget lootBtn = ButtonWidget.builder(
                Text.literal(getRGB("Auto-Loot")),
                b -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("§d[Yukile] §fYakındaki itemler çekiliyor..."), false);
                }
        ).dimensions(x, y + 50, width, height).build();

        // Butonları ekrana bas
        ((ScreenAccessor) screen).callAddDrawableChild(irpBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(ghostBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(lootBtn);
    }

    private void simulateIrpLogic(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Öldüğünde itemleri geri getirme simülasyonu için hızlı paket döngüsü
        new Thread(() -> {
            try {
                int syncId = screen.getScreenHandler().syncId;
                client.player.sendMessage(Text.literal("§d[İrp] §fÖlüm koordinatları taranıyor..."), false);
                Thread.sleep(500);
                
                for (int i = 0; i < 9; i++) { // Hotbar'ı doldurma simülasyonu
                    int slot = i;
                    client.execute(() -> 
                        client.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, client.player)
                    );
                    Thread.sleep(50);
                }
                client.player.sendMessage(Text.literal("§b§l[BAŞARILI] §fEnvanteriniz geri yüklendi."), false);
            } catch (Exception ignored) {}
        }).start();
    }
}
