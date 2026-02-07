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

    // ================== FAKE HITBOXES ==================

    // Fake Hitboxes aktif mi? (Sadece görsel, boyut değiştirmez)
    public boolean fakeHitboxesEnabled = false;

    // Fake Hitboxes rengi (isim veya HEX)
    // Örnekler: red, green, blue, yellow, aqua, purple, white, black, #FF0000, #00FF00
    public String fakeHitboxesColor = "red";

    /* -------------------------------------------------- */
    /* CHAT RENK PARSE */
    /* -------------------------------------------------- */

    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    /* -------------------------------------------------- */
    /* FAKE HITBOX COLOR -> RGB (0..1)
    /* -------------------------------------------------- */

    public static float[] parseHexOrNameToRGB(String color) {
        if (color == null || color.isBlank()) return new float[]{1f, 0f, 0f};

        color = color.trim();

        // HEX ise
        if (color.startsWith("#")) {
            try {
                int rgb = Integer.parseInt(color.substring(1), 16);
                float r = ((rgb >> 16) & 0xFF) / 255f;
                float g = ((rgb >> 8) & 0xFF) / 255f;
                float b = (rgb & 0xFF) / 255f;
                return new float[]{r, g, b};
            } catch (Exception ignored) {
                return new float[]{1f, 0f, 0f};
            }
        }

        // İsim ise
        return switch (color.toLowerCase()) {
            case "red" -> new float[]{1f, 0f, 0f};
            case "green" -> new float[]{0f, 1f, 0f};
            case "blue" -> new float[]{0f, 0f, 1f};
            case "yellow" -> new float[]{1f, 1f, 0f};
            case "aqua", "cyan" -> new float[]{0f, 1f, 1f};
            case "purple", "magenta" -> new float[]{1f, 0f, 1f};
            case "white" -> new float[]{1f, 1f, 1f};
            case "black" -> new float[]{0f, 0f, 0f};
            case "orange" -> new float[]{1f, 0.5f, 0f};
            case "pink" -> new float[]{1f, 0.4f, 0.7f};
            default -> new float[]{1f, 0f, 0f};
        };
    }

    /* -------------------------------------------------- */
    /* SABİT SES */
    /* -------------------------------------------------- */

    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
