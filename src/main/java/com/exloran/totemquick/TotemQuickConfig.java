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

    // Totem yoksa sesli uyarı
    public boolean sesliUyari = true;

    // Totem yok uyarı rengi (red, yellow, green, gold, vs.)
    public String uyarirengi = "red";

    // Fake hitbox rengi (red, blue, green, yellow, white, gray)
    public String hitboxRengi = "green";

    /* ---------------------------------------------------------------- */
    /* RENK PARSE (CHAT) */
    /* ---------------------------------------------------------------- */

    /**
     * Chat uyarı rengi
     */
    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) {
            return Formatting.RED;
        }

        Formatting formatting = Formatting.byName(color.toLowerCase());
        return formatting != null ? formatting : Formatting.RED;
    }

    /* ---------------------------------------------------------------- */
    /* HITBOX RENK PARSE (RGB) */
    /* ---------------------------------------------------------------- */

    /**
     * Fake hitbox rengi (RGB)
     * SADECE görsel amaçlıdır
     */
    public static float[] parseHitboxColor(String color) {
        if (color == null) {
            return new float[]{0.0f, 1.0f, 0.0f}; // default yeşil
        }

        return switch (color.toLowerCase()) {
            case "red" -> new float[]{1.0f, 0.0f, 0.0f};
            case "blue" -> new float[]{0.0f, 0.5f, 1.0f};
            case "yellow" -> new float[]{1.0f, 1.0f, 0.0f};
            case "white" -> new float[]{1.0f, 1.0f, 1.0f};
            case "gray" -> new float[]{0.5f, 0.5f, 0.5f};
            case "purple" -> new float[]{0.7f, 0.3f, 1.0f};
            default -> new float[]{0.0f, 1.0f, 0.0f}; // green
        };
    }

    /* ---------------------------------------------------------------- */
    /* SABİT RAHAT SES */
    /* ---------------------------------------------------------------- */

    /**
     * Tek ve rahatlatıcı uyarı sesi
     * ENTITY_EXPERIENCE_ORB_PICKUP:
     * - Kulak tırmalamaz
     * - Vanilla uyumlu
     * - PvP'de rahatsız etmez
     */
    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
