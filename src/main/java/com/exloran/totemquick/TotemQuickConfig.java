package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.util.Formatting;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    // Mod açık / kapalı
    public boolean enabled = true;  

    // Totem yoksa sesli uyarı
    public boolean sesliUyari = true;  

    // Totem yok uyarı rengi (isim veya RGB Hex: #RRGGBB)
    public String uyarirengi = "red";  

    // Totem takıldığında ses efekti olsun mu
    public boolean totemSes = true;

    // Totem takıldığında mesaj gösterilsin mi
    public boolean totemMesaj = true;

    // Mesaj ön ek simgesi (opsiyonel)
    public String mesajSimge = "✨";

    /**
     * Renk çevirici (isim veya RGB Hex) - safe
     */
    public static Formatting parseColor(String color) {
        if (color == null || color.isEmpty()) return Formatting.RED;

        // Hex RGB desteği (1.21'de ofRgb yoksa safe fallback)
        if (color.startsWith("#") && color.length() == 7) {
            try {
                int rgb = Integer.parseInt(color.substring(1), 16);
                // Eğer ofRgb çalışmazsa RED fallback
                try {
                    return Formatting.ofRgb(rgb);
                } catch (NoSuchMethodError e) {
                    return Formatting.RED;
                }
            } catch (NumberFormatException e) {
                return Formatting.RED;
            }
        }

        // İsme göre
        Formatting f = Formatting.byName(color.toUpperCase());
        return f != null ? f : Formatting.RED;
    }
}
