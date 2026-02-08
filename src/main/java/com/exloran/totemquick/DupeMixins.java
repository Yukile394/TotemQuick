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

    private static int colorTick = 0;
    private static final String[] ILLEGAL_RGB = {"§d", "§5", "§c", "§4", "§b"};

    private static String illegalRgb(String text) {
        colorTick = (colorTick + 1) % ILLEGAL_RGB.length;
        return ILLEGAL_RGB[colorTick] + text;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addIllegalInterface(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        
        // Sadece Envanter/Sandık ekranında aktif olur
        if (!(screen instanceof HandledScreen<?> handled)) return;

        int x = 8; 
        int y = 20;
        int w = 130;
        int h = 18;

        // --- 1. İRP (ITEM RESTORE PROTOCOL) ---
        ButtonWidget irpBtn = ButtonWidget.builder(
                Text.literal(illegalRgb("» İrp (İade-i İtibar)")),
                b -> {
                    sendIllegalMessage("§f[İrp] §7Ölüm verisi çekiliyor...");
                    simulatePacketExploit(handled, 50);
                }
        ).dimensions(x, y, w, h).build();

        // --- 2. NEW DUPE (CHUNK OVERLOAD) ---
        ButtonWidget dupeBtn = ButtonWidget.builder(
                Text.literal(illegalRgb("» Chunk-Drop Dupe")),
                b -> {
                    sendIllegalMessage("§f[Exploit] §7Chunk hatası tetikleniyor...");
                    simulatePacketExploit(handled, 100);
                }
        ).dimensions(x, y += 22, w, h).build();

        // --- 3. PACKET CRASHER (SUNUCU LAGLAMA) ---
        ButtonWidget crashBtn = ButtonWidget.builder(
                Text.literal(illegalRgb("» Server Desync (Lag)")),
                b -> sendIllegalMessage("§4[UYARI] §fSunucu veri akışı yavaşlatıldı.")
        ).dimensions(x, y += 22, w, h).build();

        // --- 4. UUID SPOOFER (SAHTE KİMLİK) ---
        ButtonWidget uuidBtn = ButtonWidget.builder(
                Text.literal(illegalRgb("» UUID Spoofer: ON")),
                b -> sendIllegalMessage("§b[Sistem] §fYeni kimlik tanımlandı.")
        ).dimensions(x, y += 22, w, h).build();

        // --- 5. NBT OVERFLOW (EŞYA BOZMA) ---
        ButtonWidget nbtBtn = ButtonWidget.builder(
                Text.literal(illegalRgb("» NBT Overflow")),
                b -> simulatePacketExploit(handled, 20)
        ).dimensions(x, y += 22, w, h).build();

        ((ScreenAccessor) screen).callAddDrawableChild(irpBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(dupeBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(crashBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(uuidBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(nbtBtn);
    }

    private void sendIllegalMessage(String msg) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("§d§l[ILLEGAL] " + msg), false);
        }
    }

    private void simulatePacketExploit(HandledScreen<?> screen, int loops) {
        MinecraftClient client = MinecraftClient.getInstance();
        new Thread(() -> {
            try {
                int syncId = screen.getScreenHandler().syncId;
                for (int i = 0; i < loops; i++) {
                    client.execute(() -> 
                        client.interactionManager.clickSlot(syncId, 36, 0, SlotActionType.QUICK_MOVE, client.player)
                    );
                    Thread.sleep(5); // Çok hızlı paket gönderimi
                }
                sendIllegalMessage("§a§lİŞLEM TAMAMLANDI.");
            } catch (Exception ignored) {}
        }).start();
    }
}
