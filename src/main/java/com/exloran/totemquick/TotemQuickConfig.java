package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    // Mod açık / kapalı
    public boolean enabled = true;

    // Totem yoksa sesli uyarı
    public boolean sesliUyari = true;

    // Totem yok uyarı rengi
    public String uyarirengi = "red";

    // Fake hitbox rengi
    public String hitboxRengi = "green";

    /* -------------------------------------------------- */
    /* YENİ: TOTEM UYARI COOLDOWN */
    /* -------------------------------------------------- */

    @ConfigEntry.BoundedDiscrete(min = 0, max = 200)
    @ConfigEntry.Gui.Tooltip
    public int uyarıCooldownTick = 40; // 40 tick = 2 saniye

    /* -------------------------------------------------- */
    /* RENK PARSE (CHAT) */
    /* -------------------------------------------------- */

    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) return Formatting.RED;
        Formatting f = Formatting.byName(color.toLowerCase());
        return f != null ? f : Formatting.RED;
    }

    /* -------------------------------------------------- */
    /* HITBOX RENK PARSE (SADECE GÖRSEL) */
    /* -------------------------------------------------- */

    public static float[] parseHitboxColor(String color) {
        if (color == null) return new float[]{0f, 1f, 0f};

        return switch (color.toLowerCase()) {
            case "red" -> new float[]{1f, 0f, 0f};
            case "blue" -> new float[]{0f, 0.5f, 1f};
            case "yellow" -> new float[]{1f, 1f, 0f};
            case "white" -> new float[]{1f, 1f, 1f};
            case "gray" -> new float[]{0.5f, 0.5f, 0.5f};
            case "purple" -> new float[]{0.7f, 0.3f, 1f};
            default -> new float[]{0f, 1f, 0f};
        };
    }

    /* -------------------------------------------------- */
    /* SABİT RAHATLATICI SES */
    /* -------------------------------------------------- */

    public static SoundEvent getUyariSesi() {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }
}
