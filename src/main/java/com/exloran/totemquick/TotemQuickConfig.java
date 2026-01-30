package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Formatting;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    public boolean enabled = true;

    // --- SES AYARLARI ---
    @ConfigEntry.Gui.CollapsibleObject
    public SoundSettings sound = new SoundSettings();

    public static class SoundSettings {
        public boolean sesliUyari = true;

        @ConfigEntry.Gui.Tooltip()
        public String sesID = "minecraft:block.note_block.bell"; // 1.21 yeni sesleri buraya yazılabilir

        @ConfigEntry.BoundedControl(min = 0, max = 2)
        public float pitch = 1.0f;

        @ConfigEntry.BoundedControl(min = 0, max = 1)
        public float volume = 1.0f;
    }

    // --- GÖRSEL AYARLAR ---
    @ConfigEntry.Gui.CollapsibleObject
    public VisualSettings visual = new VisualSettings();

    public static class VisualSettings {
        @ConfigEntry.Gui.Tooltip()
        public String uyarirengi = "red";

        @ConfigEntry.Gui.Tooltip()
        public String customHexColor = "#FF0000"; // Özel # hex kodu desteği

        public boolean boldText = true;
    }

    /**
     * Geliştirilmiş renk parse fonksiyonu.
     * Hem kelime (red, gold) hem de gerekirse Formatting enum desteği sağlar.
     */
    public static Formatting parseColor(String color) {
        if (color == null || color.isEmpty()) return Formatting.RED;
        
        Formatting f = Formatting.byName(color.toUpperCase());
        return f != null ? f : Formatting.RED;
    }
}
