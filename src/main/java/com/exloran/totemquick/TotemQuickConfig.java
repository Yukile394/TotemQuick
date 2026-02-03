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

    // Totem yok uyarı rengi
    public String uyarirengi = "red";

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

    // ================== YENİ AYARLAR ==================

    // Elytra görünümü gizlensin mi
    public boolean elytraGizle = false;

    // Yere atılan item büyük görünsün mü
    public boolean buyukItem = false;

    // Yere atılan item ölçeği
    public float buyukItemScale = 2.0f;
}
