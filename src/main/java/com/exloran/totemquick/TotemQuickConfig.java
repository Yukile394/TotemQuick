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

    // ================== FAKE HITBOXES (istersen kalsın) ==================

    public boolean fakeHitboxesEnabled = false;
    public String fakeHitboxesColor = "red";

    // ================== HIT COLOR ==================

    // Hit overlay renk değiştirme aktif mi?
    public boolean hitColorEnabled = true;

    // Hit overlay rengi (HEX veya isim)
    // Örnek: #ff00ff, #00ff00, red, blue, pink...
    public String hitColor = "#ff0000";

    // Hit overlay alpha (0 = görünmez, 100 = tam opak)
    public float hitAlpha = 30.0f;

    /* -------------------------------------------------- */
    /* CHAT RENK PARSE */
    /* -------------------------------------------------- */

    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    /* -------------------------------------------------- */
    /* HIT COLOR -> RGBA INT */
    /* -------------------------------------------------- */

    public static int parseHitColorToRGBA(String color, float alpha) {
        if (color == null || color.isBlank()) color = "#ff0000";

        int r = 255, g = 0, b = 0;

        color = color.trim();

        if (color.startsWith("#")) {
            try {
                int rgb = Integer.parseInt(color.substring(1), 16);
                r = (rgb >> 16) & 0xFF;
                g = (rgb >> 8) & 0xFF;
                b = rgb & 0xFF;
            } catch (Exception ignored) {
            }
        } else {
            switch (color.toLowerCase()) {
                case "green" -> { r = 0; g = 255; b = 0; }
                case "blue" -> { r = 0; g = 0; b = 255; }
                case "yellow" -> { r = 255; g = 255; b = 0; }
                case "aqua", "cyan" -> { r = 0; g = 255; b = 255; }
                case "purple", "magenta" -> { r = 255; g = 0; b = 255; }
                case "white" -> { r = 255; g = 255; b = 255; }
                case "black" -> { r = 0; g = 0; b = 0; }
                case "orange" -> { r = 255; g = 128; b = 0; }
                case "pink" -> { r = 255; g = 100; b = 180; }
            }
        }

        float a = Math.max(0f, Math.min(100f, alpha));
        int alphaByte = Math.round(255f * (a / 100f));

        return (alphaByte << 24) | (b << 16) | (g << 8) | r;
    }

    /* -------------------------------------------------- */
    /* SABİT SES */
    /* -------------------------------------------------- */

    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
