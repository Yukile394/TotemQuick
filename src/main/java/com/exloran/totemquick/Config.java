package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.util.Formatting;

@Config(name = "totemquick")
public class Config implements ConfigData {
    public boolean enabled = true;
    public boolean sesliUyari = true;
    
    // Mesaj rengi ayarı (String olarak saklayıp Formatting'e çevireceğiz)
    public String uyarirengi = "RED";
    
    // Gecikme ayarı (Sunuculardan atılmamak için)
    public int delayTicks = 0;
}

