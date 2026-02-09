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

    public boolean enabled = true;
    public boolean sesliUyari = true;

    /* -------------------------------------------------- */
    /* CHAT */
    /* -------------------------------------------------- */

    public String uyarirengi = "red";

    /* -------------------------------------------------- */
    /* HUD POZİSYON / BOYUT */
    /* -------------------------------------------------- */

    // HUD offset (DupeMixins kullanıyor)
    public int hudOffsetX = 0;
    public int hudOffsetY = 0;

    // HUD ölçek
    public float hudScale = 1.0f;

    /* -------------------------------------------------- */
    /* CAN / HIT ANİMASYON */
    /* -------------------------------------------------- */

    // Hit rengi (SARI)
    public String hitColor = "#FFD500";
    public float hitAlpha = 40f;

    // Can azalırken animasyon rengi (YEŞİL)
    public String healthAnimColor = "#00FF55";

    /* -------------------------------------------------- */
    /* TARGET / HITBOX ORTASI 🍭 */
    /* -------------------------------------------------- */

    // Hitbox ortası sembol açık mı
    public boolean targetEnabled = true;

    // Döndürme hızı
    public float targetRotateSpeed = 3.0f;

    // Ortadaki sembol
    public String targetSymbol = "🍭";

    // Sembol rengi
    public String targetColor = "#FFD500";

    // Sembol alpha
    public float targetAlpha = 100f;

    /* -------------------------------------------------- */
    /* YARDIMCI */
    /* -------------------------------------------------- */

    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    public static int parseHitColorToRGBA(String color, float alpha) {
        if (color == null || color.isBlank()) color = "#FFD500";

        int r = 255, g = 213, b = 0;

        if (color.startsWith("#")) {
            try {
                int rgb = Integer.parseInt(color.substring(1), 16);
                r = (rgb >> 16) & 0xFF;
                g = (rgb >> 8) & 0xFF;
                b = rgb & 0xFF;
            } catch (Exception ignored) {}
        }

        int a = Math.round(Math.max(0, Math.min(100, alpha)) * 2.55f);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /* -------------------------------------------------- */
    /* SES */
    /* -------------------------------------------------- */

    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
