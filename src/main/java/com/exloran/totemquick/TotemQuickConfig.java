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
        @ConfigEntry.Gui.Tooltip()
        public boolean sesliUyari = true;

        @ConfigEntry.Gui.Tooltip()
        public String sesID = "minecraft:block.note_block.bell"; // 1.21 Ses ID'si

        @ConfigEntry.BoundedControl(min = 0, max = 2)
        public float pitch = 1.0f; // Ses inceliği/kalınlığı
        
        @ConfigEntry.BoundedControl(min = 0, max = 1)
        public float volume = 1.0f;
    }

    // --- GÖRSEL AYARLAR ---
    @ConfigEntry.Gui.CollapsibleObject
    public VisualSettings visual = new VisualSettings();

    public static class VisualSettings {
        @ConfigEntry.Gui.Tooltip()
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        public Formatting uyarirengi = Formatting.RED;

        public boolean boldText = true;
    }

    /**
     * Minecraft Formatting nesnesini doğrudan döndürür.
     * Enum kullandığımız için parse hatası riski kalmadı.
     */
    public Formatting getSelectedColor() {
        return visual.uyarirengi != null ? visual.uyarirengi : Formatting.RED;
    }
}
