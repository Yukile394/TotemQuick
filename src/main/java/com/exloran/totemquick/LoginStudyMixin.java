package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget; // dış yapı bozulmasın diye duruyor
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class LoginStudyMixin {

    // Dış yapı bozulmasın diye alanlar duruyor
    private TextFieldWidget passwordField;
    private boolean isAuthorized = false;

    // Döngüyle gezilecek renkler
    private static final String[] COLORS = new String[]{
            "red", "green", "blue", "yellow", "aqua", "purple", "white", "black", "orange", "pink"
    };

    @Inject(method = "init", at = @At("TAIL"))
    private void addSecurityLayer(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;

        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

        // Fake Hitboxes Aç / Kapat
        ButtonWidget toggleBtn = ButtonWidget.builder(
                Text.literal(getToggleText(config.fakeHitboxesEnabled)),
                btn -> {
                    config.fakeHitboxesEnabled = !config.fakeHitboxesEnabled;
                    AutoConfig.getConfigHolder(TotemQuickConfig.class).save();

                    btn.setMessage(Text.literal(getToggleText(config.fakeHitboxesEnabled)));

                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.sendMessage(
                                Text.literal(config.fakeHitboxesEnabled
                                        ? "§aFake Hitboxes: AÇIK (sabit boyut, sadece görsel)"
                                        : "§cFake Hitboxes: KAPALI"),
                                false
                        );
                    }
                }
        ).dimensions(
                screen.width / 2 - 70,
                screen.height / 2 - 20,
                140,
                20
        ).build();

        // Renk Değiştir
        ButtonWidget colorBtn = ButtonWidget.builder(
                Text.literal("§eRenk: " + config.fakeHitboxesColor),
                btn -> {
                    String next = getNextColor(config.fakeHitboxesColor);
                    config.fakeHitboxesColor = next;
                    AutoConfig.getConfigHolder(TotemQuickConfig.class).save();

                    btn.setMessage(Text.literal("§eRenk: " + next));

                    if (MinecraftClient.getInstance().player != null) {
                        MinecraftClient.getInstance().player.sendMessage(
                                Text.literal("§bFake Hitboxes Rengi: §f" + next),
                                false
                        );
                    }
                }
        ).dimensions(
                screen.width / 2 - 70,
                screen.height / 2 + 5,
                140,
                20
        ).build();

        ((ScreenAccessor) screen).callAddDrawableChild(toggleBtn);
        ((ScreenAccessor) screen).callAddDrawableChild(colorBtn);
    }

    private String getToggleText(boolean enabled) {
        return enabled ? "§aFake Hitboxes: AÇIK" : "§cFake Hitboxes: KAPALI";
    }

    private String getNextColor(String current) {
        for (int i = 0; i < COLORS.length; i++) {
            if (COLORS[i].equalsIgnoreCase(current)) {
                return COLORS[(i + 1) % COLORS.length];
            }
        }
        return COLORS[0];
    }
}
