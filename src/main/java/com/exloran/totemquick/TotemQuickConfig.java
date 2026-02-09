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

    /* ================== HIT EFFECT (YENİ) ================== */

    // Vurunca ekran flash aktif mi
    public boolean hitFlashEnabled = true;

    // Hit flash rengi (HEX)
    // Örnek: #FFFF00 (sarı), #00FF00 (yeşil)
    public String hitFlashColor = "#FFFF00";

    // Hit flash alpha (0-100)
    public float hitFlashAlpha = 35.0f;

    /* ================== HITBOX ORTA SİMGESİ ================== */

    // ✯ simgesi açık mı
    public boolean centerSymbolEnabled = true;

    // Sembol (✯, 🍭, ★ vb.)
    public String centerSymbol = "✯";

    // Sembol rengi (HEX)
    public String centerSymbolColor = "#00FF6A";

    // Sembol boyutu
    public float centerSymbolScale = 1.2f;

    // Dönme hızı (küçük = yavaş)
    public float centerSymbolRotateSpeed = 0.015f;

    // Konum offset (hitbox ortasına göre)
    public int centerOffsetX = 0;
    public int centerOffsetY = 0;

    /* -------------------------------------------------- */
    /* CHAT RENK PARSE (AYNI KALDI) */
    /* -------------------------------------------------- */

    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    /* -------------------------------------------------- */
    /* HEX -> ARGB (GENEL RENK PARSER) */
    /* -------------------------------------------------- */

    public static int parseHexColor(String hex, float alphaPercent) {
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
