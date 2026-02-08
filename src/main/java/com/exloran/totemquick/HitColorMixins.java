package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public abstract class HitColorMixins {

    @Inject(method = "render", at = @At("TAIL"))
    private void drawHitOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
        try {
            TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
            if (!config.hitColorEnabled) return;

            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            if (player == null) return;

            if (player.hurtTime > 0) {
                int color = TotemQuickConfig.parseHitColorToRGBA(config.hitColor, config.hitAlpha);
                int w = client.getWindow().getScaledWidth();
                int h = client.getWindow().getScaledHeight();
                context.fill(0, 0, w, h, color);
            }

            // ------------------- Map Kopyala Butonu -------------------
            Screen screen = (Screen) (Object) this; // Mixin hack
            int buttonWidth = 120;
            int buttonHeight = 20;
            int x = screen.width / 2 - buttonWidth / 2;
            int y = screen.height - 50;

            // ESC altında deneme butonu
            screen.addDrawableChild(new ButtonWidget(
                    x, y, buttonWidth, buttonHeight,
                    Text.literal("Map Kopyala"),
                    button -> {
                        if (client.player != null && !client.player.getMainHandStack().isEmpty()) {
                            client.player.getInventory().insertStack(client.player.getMainHandStack().copy());
                            client.player.sendMessage(Text.literal("Harita kopyalandı!"), true);
                        }
                    },
                    ButtonWidget.DEFAULT_NARRATION_SUPPLIER
            ));
            // ----------------------------------------------------------

        } catch (Exception e) {
            // Mobilde çökme olmasın, loga yaz
            System.out.println("[HitColorMixins] Hata: " + e.getMessage());
        }
    }
}
