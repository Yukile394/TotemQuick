package com.exloran.totemquick;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final String MOD_ID = "totemquick";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Config kaydı
        AutoConfig.register(Config.class, GsonConfigSerializer::new);
        
        // Totem mantığını başlat
        TotemManager.init();
        
        LOGGER.info("TotemQuick | Baslatildi!");
    }
}

