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

    // === THREAD ===
    private static final ExecutorService DUPE_EXECUTOR = Executors.newFixedThreadPool(4);

    // === STATES (V2) ===
    private static boolean DUPE_ENABLED = false;
    private static boolean DESYNC = false;
    private static boolean SEND_PACKETS = true;
    private static boolean DELAY_PACKETS = false;

    // === RGB PEMBE V2 ===
    private static int rgbTick = 0;
    private static final String[] RGB = {"§d", "§5", "§d", "§f"};

    private static String rgb(String s) {
        rgbTick = (rgbTick + 1) % RGB.length;
        return RGB[rgbTick] + s;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addUltisStyleUI(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        if (!(screen instanceof HandledScreen<?> handled)) return;

        int x = 8;
        int y = 8;
        int w = 150;
        int h = 18;

        // === DUPE ON/OFF ===
        ButtonWidget toggleBtn = ButtonWidget.builder(
                Text.literal(rgb("DUPE: " + (DUPE_ENABLED ? "ON" : "OFF"))),
                b -> {
                    DUPE_ENABLED = !DUPE_ENABLED;
                    b.setMessage(Text.literal(rgb("DUPE: " + (DUPE_ENABLED ? "ON" : "OFF"))));
                }
        ).dimensions(x, y, w, h).build();

        // === DE-SYNC ===
        ButtonWidget desyncBtn = ButtonWidget.builder(
                Text.literal(rgb("De-sync: " + DESYNC)),
                b -> {
                    DESYNC = !DESYNC;
                    b.setMessage(Text.literal(rgb("De-sync: " + DESYNC)));
                }
        ).dimensions(x, y += 20, w, h).build();

        // === SEND PACKETS ===
        ButtonWidget sendBtn = ButtonWidget.builder(
                Text.literal(rgb("Send packets: " + SEND_PACKETS)),
                b -> {
                    SEND_PACKETS = !SEND_PACKETS;
                    b.setMessage(Text.literal(rgb("Send packets: " + SEND_PACKETS)));
                }
        ).dimensions(x, y += 20, w, h).build();

        // === DELAY PACKETS ===
        ButtonWidget delayBtn = ButtonWidget.builder(
                Text.literal(rgb("Delay packets: " + DELAY_PACKETS)),
                b -> {
                    DELAY_PACKETS = !DELAY_PACKETS;
                    b.setMessage(Text.literal(rgb("Delay packets: " + DELAY_PACKETS)));
                }
        ).dimensions(x, y += 20, w, h).build();

        // === SAVE GUI ===
        ButtonWidget saveBtn = ButtonWidget.builder(
                Text.literal(rgb("Save GUI")),
                b -> {
                    MinecraftClient.getInstance().player.sendMessage(
                            Text.literal("§d[ULTIS V2] §fGUI state saved."),
                            false
                    );
                }
        ).dimensions(x, y += 20, w, h).build();

        // === DISCONNECT & SEND ===
        ButtonWidget discBtn = ButtonWidget.builder(
                Text.literal(rgb("Disconnect & send")),
                b -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        client.player.sendMessage(
                                Text.literal("§d[ULTIS V2] §fSoft disconnect simulate."),
                                false
                        );
                    }
                }
        ).dimensions(x, y += 20, w, h).build();

        // === FABRICATE PACKET (V2) ===
        ButtonWidget fabBtn = ButtonWidget.builder(
                Text.literal(rgb("Fabricate packet")),
                b -> runUltisDupeV2(handled)
        ).dimensions(x, y += 20, w, h).build();

        ((ScreenAccessor) screen).callAddDrawableChild(toggleBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(desyncBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(sendBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(delayBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(saveBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(discBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(fabBtn);
    }

    // === V2 MANTIK (KONTROLLÜ BURST) ===
    private void runUltisDupeV2(HandledScreen<?> screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!DUPE_ENABLED || client.player == null || client.interactionManager == null) return;

        int delay = DELAY_PACKETS ? 8 : 2;
        int loops = DESYNC ? 15 : 30;

        DUPE_EXECUTOR.execute(() -> {
            try {
                int syncId = screen.getScreenHandler().syncId;
                for (int i = 0; i < loops; i++) {
                    if (SEND_PACKETS) {
                        client.execute(() ->
                                client.interactionManager.clickSlot(
                                        syncId,
                                        36,
                                        0,
                                        SlotActionType.QUICK_MOVE,
                                        client.player
                                )
                        );
                    }
                    Thread.sleep(delay);
                }
            } catch (InterruptedException ignored) {}
        });

        client.player.sendMessage(
                Text.literal("§d§l[ULTIS V2] §fPacket cycle executed."),
                false
        );
    }
            }
