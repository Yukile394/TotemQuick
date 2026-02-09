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

    /* ================== HIT FLASH ================== */

    public boolean hitFlashEnabled = true;

    // Sarı default
    public String hitFlashColor = "#FFFF00";

    public float hitFlashAlpha = 35.0f;

    /* ================== HITBOX ORTA ✯ ================== */

    // ✯ açık mı
    public boolean centerSymbolEnabled = true;

    // ✯ karakteri
    public String starSymbol = "✯";

    // ✯ rengi
    public String starColor = "#00FF6A";

    // ✯ boyutu
    public float starScale = 1.2f;

    // ✯ dönme hızı (YAVAŞ)
    public float starRotateSpeed = 0.015f;

    // ✯ pozisyon offset
    public int starOffsetX = 0;
    public int starOffsetY = 0;

    /* -------------------------------------------------- */
    /* CHAT RENK PARSE */
    /* -------------------------------------------------- */

    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    /* -------------------------------------------------- */
    /* HEX -> ARGB  (Mixin uyumlu) */
    /* -------------------------------------------------- */

    public static int parseHex(String hex, float alphaPercent) {
        if (hex == null || !hex.startsWith("#")) hex = "#FFFFFF";

        int rgb;
        try {
            rgb = Integer.parseInt(hex.substring(1), 16);
        } catch (Exception e) {
            rgb = 0xFFFFFF;
        }

        int a = Math.min(255, Math.max(0, (int)(255f * (alphaPercent / 100f))));
        return (a << 24) | rgb;
    }

    /* -------------------------------------------------- */
    /* SABİT SES */
    /* -------------------------------------------------- */

    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
