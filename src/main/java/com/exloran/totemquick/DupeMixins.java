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

    // Thread havuzu (AYNI)
    private static final ExecutorService DUPE_EXECUTOR = Executors.newFixedThreadPool(4);

    // DUPE AÇ/KAPA DURUMU (YENİ)
    private static boolean DUPE_ENABLED = false;

    @Inject(method = "init", at = @At("TAIL"))
    private void addUltisStyleUI(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        if (!(screen instanceof HandledScreen<?> handled)) return;

        int startY = 10;

        // === DUPE AÇ/KAPA BUTONU (SOLDA) ===
        ButtonWidget toggleBtn = ButtonWidget.builder(
                Text.literal(DUPE_ENABLED ? "§aDUPE: ON" : "§cDUPE: OFF"),
                btn -> {
                    DUPE_ENABLED = !DUPE_ENABLED;
                    btn.setMessage(Text.literal(DUPE_ENABLED ? "§aDUPE: ON" : "§cDUPE: OFF"));
                }
        ).dimensions(10, startY, 110, 20).build();

        // === ANA BUTON: ULTRA DUPE ===
        ButtonWidget dupeBtn = ButtonWidget.builder(
                Text.literal("§d§l» ULTRA DUPE «"),
                btn -> {
                    if (!DUPE_ENABLED) {
                        if (MinecraftClient.getInstance().player != null) {
                            MinecraftClient.getInstance().player.sendMessage(
                                    Text.literal("§cDupe kapalı! Önce aç."),
                                    false
                            );
                        }
                        return;
                    }
                    runUltisDupe(handled);
                }
        ).dimensions(10, startY + 25, 110, 20).build();

        // === YARDIMCI BUTON: AUTO PV ===
        ButtonWidget pvBtn = ButtonWidget.builder(
                Text.literal("§b§l» AUTO PV 1 «"),
                btn -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null && client.player.networkHandler != null) {
                        client.player.networkHandler.sendCommand("pv 1");
                    }
                }
        ).dimensions(10, startY + 50, 110, 20).build();

        ((ScreenAccessor) screen).callAddDrawableChild(toggleBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(dupeBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(pvBtn);
    }

    private void runUltisDupe(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null) return;

        client.player.sendMessage(
                Text.literal("§d§l[ULTIS] §fSaldırı başlatıldı. Senkronizasyon kırılıyor..."),
                false
        );

        for (int t = 0; t < 4; t++) {
            DUPE_EXECUTOR.execute(() -> {
                try {
                    for (int i = 0; i < 40; i++) {
                        int syncId = screen.getScreenHandler().syncId;

                        client.execute(() -> {
                            client.interactionManager.clickSlot(
                                    syncId,
                                    36,
                                    0,
                                    SlotActionType.QUICK_MOVE,
                                    client.player
                            );
                        });

                        Thread.sleep(2);
                    }
                } catch (InterruptedException ignored) {
                }
            });
        }

        client.player.sendMessage(
                Text.literal("§a§l[BAŞARILI] §fPaketler hedefe ulaştı!"),
                false
        );
    }
}
