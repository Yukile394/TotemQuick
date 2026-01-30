package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    // Mod açık / kapalı
    public boolean enabled = true;  

    // Totem yoksa sesli uyarı
    public boolean sesliUyari = true;  

    // Totem yok uyarı rengi (isim veya RGB Hex: #RRGGBB)
    public String uyarirengi = "red";  

    // Yeni: Totem takıldığında ses efekti olsun mu
    public boolean totemSes = true;

    // Yeni: Totem takıldığında mesaj gösterilsin mi
    public boolean totemMesaj = true;

    // Yeni: Mesaj ön ek simgesi (opsiyonel, örn: ⚡, ✨, 🛡️)
    public String mesajSimge = "✨";

    /**
     * Bu fonksiyon renk string'ini Minecraft Formatting veya RGB değerine çevirir.
     * Örnek kullanım:
     *   String renk = config.uyarirengi;
     *   Formatting renkFormat = TotemQuickConfig.parseColor(renk);
     */
    public static Formatting parseColor(String color) {
        if (color == null || color.isEmpty()) return Formatting.RED;

        // Eğer #RRGGBB şeklindeyse: RGB ile uyumlu TextColor oluştur (Minecraft 1.19+)
        if (color.startsWith("#") && color.length() == 7) {
            try {
                int rgb = Integer.parseInt(color.substring(1), 16);
                return Formatting.ofRgb(rgb);  // 1.21+ ile uyumlu
            } catch (NumberFormatException e) {
                return Formatting.RED;
            }
        }

        // İsme göre
        Formatting f = Formatting.byName(color.toUpperCase());
        return f != null ? f : Formatting.RED;
    }
}
