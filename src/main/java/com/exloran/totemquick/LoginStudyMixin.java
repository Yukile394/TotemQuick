package com.exloran.totemquick.mixin;

import com.exloran.totemquick.TotemQuickConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget; // dış yapı bozulmasın diye duruyor
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class LoginStudyMixin {

    // Dış yapı bozulmasın diye alanlar duruyor
    private TextFieldWidget passwordField;
    private boolean isAuthorized = false;

    // Döngüyle gezilecek renkler (config için referans dursun)
    private static final String[] COLORS = new String[]{
            "red", "green", "blue", "yellow", "aqua", "purple", "white", "black", "orange", "pink"
    };

    @Inject(method = "init", at = @At("TAIL"))
    private void addSecurityLayer(CallbackInfo ci) {
        // Artık buton yok.
        // Sadece config okunuyor, render mixin bu ayarları kullanacak.
        TotemQuickConfig config = AutoConfig.getConfigHolder(TotemQuickConfig.class).getConfig();

        // Güvenlik: renk null ise varsayılana çek
        if (config.fakeHitboxesColor == null || config.fakeHitboxesColor.isEmpty()) {
            config.fakeHitboxesColor = COLORS[0];
            AutoConfig.getConfigHolder(TotemQuickConfig.class).save();
        }

        // Burada bilerek hiçbir şey eklemiyoruz (UI temiz kalsın diye)
    }

    // Yardımcılar dursun, dış yapı bozulmasın
    private String getToggleText(boolean enabled) {
        return enabled ? "§aFake Hitboxes: AÇIK" : "§cFake Hitboxes: KAPALI";
    }

    private String getNextColor(String current) {
        if (current == null) return COLORS[0];

        for (int i = 0; i < COLORS.length; i++) {
            if (COLORS[i].equalsIgnoreCase(current)) {
                return COLORS[(i + 1) % COLORS.length];
            }
        }
        return COLORS[0];
    }
}
