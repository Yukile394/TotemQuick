package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    /* -------------------------------------------------- */
    /* GENEL */
    /* -------------------------------------------------- */

    // Mod açık / kapalı
    public boolean enabled = true;

    // Sesli uyarı
    public boolean sesliUyari = true;

    /* -------------------------------------------------- */
    /* CHAT */
    /* -------------------------------------------------- */

    // Totem yok mesaj rengi
    // red, green, yellow, aqua, white...
    public String uyarirengi = "red";

    /* -------------------------------------------------- */
    /* HUD / HIT GÖSTERGESİ */
    /* -------------------------------------------------- */

    // HUD aktif mi
    public boolean hudEnabled = true;

    // HUD boyutu (1.0 = normal)
    // 0.5 küçük | 1.5 büyük
    public float hudScale = 1.0f;

    // HUD ekran konumu
    // X = soldan sağa
    // Y = yukarıdan aşağı
    public int hudX = 10;
    public int hudY = 10;

    // Merkezden mi hizalansın?
    public boolean centerHud = false;

    /* -------------------------------------------------- */
    /* RENKLER */
    /* -------------------------------------------------- */

    // Hit alındığında renk (varsayılan: SARI)
    // Hex veya isim
    public String hitColor = "#FFD500";

    // Hit efekti alpha (0-100)
    public float hitAlpha = 40.0f;

    // Animasyon rengi (varsayılan: YEŞİL)
    public String animationColor = "#00FF55";

    // Nick rengi
    public String nickColor = "white";

    /* -------------------------------------------------- */
    /* ANİMASYON */
    /* -------------------------------------------------- */

    // Animasyon açık mı
    public boolean animationEnabled = true;

    // Animasyon süresi (tick)
    // 20 tick = 1 saniye
    public int animationDuration = 15;

    // Hit alınca büyüme efekti
    public boolean scaleAnimation = true;

    // Büyüme miktarı
    // 1.1 = %10 büyür
    public float scaleMultiplier = 1.1f;

    /* -------------------------------------------------- */
    /* YARDIMCI METODLAR */
    /* -------------------------------------------------- */

    // Chat rengi parse
    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    // HEX / isim → ARGB
    public static int parseColorToARGB(String color, float alpha) {
        if (color == null || color.isBlank()) color = "#FFD500";

        int r = 255, g = 255, b = 0;

        if (color.startsWith("#")) {
            try {
                int rgb = Integer.parseInt(color.substring(1), 16);
                r = (rgb >> 16) & 0xFF;
                g = (rgb >> 8) & 0xFF;
                b = rgb & 0xFF;
            } catch (Exception ignored) {}
        } else {
            switch (color.toLowerCase()) {
                case "green" -> { r = 0; g = 255; b = 0; }
                case "yellow" -> { r = 255; g = 255; b = 0; }
                case "red" -> { r = 255; g = 0; b = 0; }
                case "blue" -> { r = 0; g = 0; b = 255; }
                case "aqua", "cyan" -> { r = 0; g = 255; b = 255; }
                case "pink" -> { r = 255; g = 100; b = 180; }
                case "white" -> { r = 255; g = 255; b = 255; }
                case "black" -> { r = 0; g = 0; b = 0; }
            }
        }

        float a = Math.max(0f, Math.min(100f, alpha));
        int alphaByte = Math.round(255f * (a / 100f));

        return (alphaByte << 24) | (r << 16) | (g << 8) | b;
    }

    /* -------------------------------------------------- */
    /* SES */
    /* -------------------------------------------------- */

    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
