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

    /**
     * Güvenli renk parse fonksiyonu
     * String olarak verilen rengi Minecraft Formatting objesine çevirir
     * Eğer renk hatalıysa varsayılan RED döner
     */
    public static net.minecraft.util.Formatting parseColor(String color) {
        if (color == null || color.isEmpty()) return net.minecraft.util.Formatting.RED;

        // İsimle eşleşme
        net.minecraft.util.Formatting f = net.minecraft.util.Formatting.byName(color.toUpperCase());
        return f != null ? f : net.minecraft.util.Formatting.RED;
    }
}
