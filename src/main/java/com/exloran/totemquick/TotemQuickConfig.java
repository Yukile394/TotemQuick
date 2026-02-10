package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    // Mod açık / kapalı
    public boolean enabled = true;

    // Sesli uyarı
    public boolean sesliUyari = true;

    // Totem yok uyarı rengi
    public String uyarirengi = "red";

    /* ================== KEYBOARD HUD ================== */

    // HUD açık mı
    public boolean keyboardHudEnabled = true;

    // HUD boyutu (0.3 = küçük, 1.0 = büyük)
    public float keyboardHudScale = 0.45f;

    // HUD X konumu
    public int keyboardHudX = 10;

    // HUD Y konumu
    public int keyboardHudY = 20;

    /* -------------------------------------------------- */

    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
