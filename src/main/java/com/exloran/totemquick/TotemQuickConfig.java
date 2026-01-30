package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.Formatting;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean enabled = true;

    // --- SES AYARLARI ---
    @ConfigEntry.Gui.Tooltip
    public boolean sesliUyari = true;

    @ConfigEntry.Gui.Tooltip
    public String sesID = "minecraft:block.note_block.bell";

    @ConfigEntry.BoundedControl(min = 0, max = 2)
    public float soundPitch = 1.0f;

    @ConfigEntry.BoundedControl(min = 0, max = 1)
    public float soundVolume = 1.0f;

    // --- GÖRSEL AYARLAR ---
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
    public Formatting uyarirengi = Formatting.RED;

    @ConfigEntry.Gui.Tooltip
    public String customHexColor = "#FF0000";

    public boolean boldText = true;
}
