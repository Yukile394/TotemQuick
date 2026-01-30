package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    // Mod açık / kapalı
    public boolean enabled = true;

    // Totem yoksa sesli uyarı
    public boolean sesliUyari = true;

    // Totem yok uyarı rengi (red, yellow, green, gold, vs.)
    public String uyarirengi = "red";
}
