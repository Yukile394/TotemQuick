package com.exloran.totemquick;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {

    public static final String MOD_ID = "totemquick";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // ✅ DOĞRU config kaydı
        AutoConfig.register(TotemQuickConfig.class, GsonConfigSerializer::new);

        // Totem mantığını başlat
        TotemManager.init();

        LOGGER.info("TotemQuick | Başlatıldı!");
    }
}
