package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class HitColorMixins {

    @Inject(method = "render", at = @At("TAIL"))
    private void totemquick$drawHitColorOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        // === Hit Color Overlay ===
        if (config.hitColorEnabled && player.hurtTime > 0) {
            int color = TotemQuickConfig.parseHitColorToRGBA(config.hitColor, config.hitAlpha);
            int w = client.getWindow().getScaledWidth();
            int h = client.getWindow().getScaledHeight();
            context.fill(0, 0, w, h, color);
        }
    }

    // === ESC ekranında "Map Kopyala" tuşu ekle ===
    @Inject(method = "init", at = @At("TAIL"))
    private void totemquick$addMapCopyButton(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(Object) this instanceof Screen screen)) return;

        int buttonWidth = 100;
        int buttonHeight = 20;
        int x = screen.width / 2 - buttonWidth / 2;
        int y = screen.height - 50;

        screen.addDrawableChild(new ButtonWidget(x, y, buttonWidth, buttonHeight, "Map Kopyala", button -> {
            PlayerEntity player = client.player;
            if (player != null) {
                ItemStack mainHand = player.getMainHandStack();
                if (mainHand.getItem() == Items.FILLED_MAP) {
                    player.getInventory().insertStack(mainHand.copy());
                    client.player.sendMessage(
                        net.minecraft.text.Text.literal("Harita kopyalandı!"), true
                    );
                }
            }
        }));
    }
}
