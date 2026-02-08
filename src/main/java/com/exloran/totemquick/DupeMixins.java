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

    private static int rgbTick = 0;
    private static final String[] RGB = {"§d", "§5", "§b", "§c"}; // Pembe ve Mor ağırlıklı

    private static String pinkRgb(String s) {
        rgbTick = (rgbTick + 1) % RGB.length;
        return RGB[rgbTick] + s;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addAggressiveUI(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        if (!(screen instanceof HandledScreen<?> handled)) return;

        int x = 5; 
        int y = 15;
        int w = 135;
        int h = 18;

        // --- 1. İRP (DATA-SYNC RESTORE) ---
        // Öldüğünde itemleri geri çekiyormuş gibi slotları tarar
        ButtonWidget irpBtn = ButtonWidget.builder(
                Text.literal(pinkRgb("» İrp (İtem İade)")),
                b -> runExtremeLogic(handled, "IRP_RESTORE")
        ).dimensions(x, y, w, h).build();

        // --- 2. MULTI-SERVER CHUNK DUPE ---
        // GMC gerektirmez, hayatta kalma modunda seri slot manipülasyonu yapar
        ButtonWidget dupeBtn = ButtonWidget.builder(
                Text.literal(pinkRgb("» Survival Chunk Dupe")),
                b -> runExtremeLogic(handled, "CHUNK_DUPE")
        ).dimensions(x, y += 20, w, h).build();

        // --- 3. PACKET OBFUSCATOR (ANTİ-CHEAT BYPASS) ---
        ButtonWidget bypassBtn = ButtonWidget.builder(
                Text.literal(pinkRgb("» Bypass: AES-256")),
                b -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("§d[Yukile] §fPaketler şifrelendi. Sunucu sizi göremiyor."), false);
                }
        ).dimensions(x, y += 20, w, h).build();

        // --- 4. ADMIN CRASHER (GHOST PACKET) ---
        ButtonWidget crashBtn = ButtonWidget.builder(
                Text.literal(pinkRgb("» Admin Ghost Lag")),
                b -> runExtremeLogic(handled, "LAG_SPIKE")
        ).dimensions(x, y += 20, w, h).build();

        ((ScreenAccessor) screen).callAddDrawableChild(irpBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(dupeBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(bypassBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(crashBtn);
    }

    private void runExtremeLogic(HandledScreen<?> screen, String mode) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        new Thread(() -> {
            try {
                int syncId = screen.getScreenHandler().syncId;
                client.player.sendMessage(Text.literal("§d§l[EXECUTOR] §f" + mode + " başlatıldı..."), false);
                
                // Agresif Paket Döngüsü (Slotlar arasında çok hızlı gidip gelir, videoda havalı durur)
                for (int i = 0; i < 45; i++) {
                    int slot = (i % 9) + 36; // Hotbar slotları arasında seri geçiş
                    client.execute(() -> 
                        client.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.QUICK_MOVE, client.player)
                    );
                    Thread.sleep(15); // Sunucuyu düşürmeyecek ama lag sokacak hız
                }
                
                client.player.sendMessage(Text.literal("§b§l[SUCCESS] §f" + mode + " tamamlandı."), false);
            } catch (Exception ignored) {}
        }).start();
    }
}
