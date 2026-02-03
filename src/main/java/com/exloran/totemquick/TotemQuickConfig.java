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

    // Sesli uyarı açık mı
    public boolean sesliUyari = true;

    // Totem yok uyarı rengi (chat için)
    public String uyarirengi = "red";

    // Elytra görünümü gizlensin mi
    public boolean elytraGizle = false;

    // ================== HIT COLOR ==================

    // HitColor aktif mi
    public boolean hitColorEnabled = true;

    // Hit rengi (isim veya hex): red, yellow, #FF0000, #00FF00 vs.
    public String hitColor = "#FF0000";

    /* -------------------------------------------------- */
    /* RENK PARSE (CHAT) */
    /* -------------------------------------------------- */

    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    /* -------------------------------------------------- */
    /* SABİT RAHATLATICI SES */
    /* -------------------------------------------------- */

    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
