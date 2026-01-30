package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config; // Notasyon olan

@Config(name = "totemquick")
public class Config implements ConfigData {
    public boolean enabled = true;
    public boolean sesliUyari = true;
    public String uyarirengi = "RED";
    public int delayTicks = 0;
}
