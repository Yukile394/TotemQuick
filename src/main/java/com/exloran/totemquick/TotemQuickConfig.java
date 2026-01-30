package com.exloran.totemquick;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

@Config(name = "totemquick")
public class TotemQuickConfig implements ConfigData {

    // Mod açık / kapalı
    public boolean enabled = true;

    // Totem yoksa sesli uyarı
    public boolean sesliUyari = true;

    // Totem yok uyarı rengi (red, yellow, green, gold, vs.)
    public String uyarirengi = "red";

    // Seçili uyarı sesi (aşağıdaki listeden biri)
    public String uyariSesi = "ENTITY_EXPERIENCE_ORB_PICKUP";

    /* ---------------------------------------------------------------- */
    /* RENK PARSE */
    /* ---------------------------------------------------------------- */

    /**
     * String olarak verilen rengi Minecraft Formatting objesine çevirir
     * Hatalıysa varsayılan RED döner
     */
    public static Formatting parseColor(String color) {
        if (color == null || color.isBlank()) {
            return Formatting.RED;
        }

        Formatting formatting = Formatting.byName(color.toLowerCase());
        return formatting != null ? formatting : Formatting.RED;
    }

    /* ---------------------------------------------------------------- */
    /* SES PARSE */
    /* ---------------------------------------------------------------- */

    /**
     * Config'te yazan sesi güvenli şekilde SoundEvent'e çevirir
     * Hatalıysa varsayılan XP sesi döner
     */
    public static SoundEvent parseSound(String soundName) {
        if (soundName == null || soundName.isBlank()) {
            return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
        }

        return switch (soundName.toUpperCase()) {

            // 1️⃣ XP orb alma – hafif uyarı
            case "ENTITY_EXPERIENCE_ORB_PICKUP" ->
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;

            // 2️⃣ Totem kullanma sesi – mantıklı ve güçlü
            case "ITEM_TOTEM_USE" ->
                    SoundEvents.ITEM_TOTEM_USE;

            // 3️⃣ Anvil düşme – sert uyarı
            case "BLOCK_ANVIL_LAND" ->
                    SoundEvents.BLOCK_ANVIL_LAND;

            // 4️⃣ Beacon aktif – dikkat çekici
            case "BLOCK_BEACON_ACTIVATE" ->
                    SoundEvents.BLOCK_BEACON_ACTIVATE;

            // 5️⃣ Enderman uyarı sesi – tehlike hissi
            case "ENTITY_ENDERMAN_STARE" ->
                    SoundEvents.ENTITY_ENDERMAN_STARE;

            // 6️⃣ Guardian uyarı – yüksek alarm
            case "ENTITY_GUARDIAN_ATTACK" ->
                    SoundEvents.ENTITY_GUARDIAN_ATTACK;

            // 7️⃣ Wither spawn – aşırı tehlike
            case "ENTITY_WITHER_SPAWN" ->
                    SoundEvents.ENTITY_WITHER_SPAWN;

            // 8️⃣ Bell çalma – net bildirim
            case "BLOCK_BELL_USE" ->
                    SoundEvents.BLOCK_BELL_USE;

            // 9️⃣ Iron Golem hasar – sert uyarı
            case "ENTITY_IRON_GOLEM_HURT" ->
                    SoundEvents.ENTITY_IRON_GOLEM_HURT;

            // 🔟 Dragon growl – maksimum alarm
            case "ENTITY_ENDER_DRAGON_GROWL" ->
                    SoundEvents.ENTITY_ENDER_DRAGON_GROWL;

            // Varsayılan
            default ->
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
        };
    }
    }
