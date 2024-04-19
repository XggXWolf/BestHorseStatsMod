package net.xggxwolf.besthorsestats;

import net.fabricmc.api.ClientModInitializer;
import net.xggxwolf.besthorsestats.utils.FindBestHorse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BestHorseStats implements ClientModInitializer {
    public static final String MOD_ID = "besthorsestats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        new FindBestHorse().register();
    }
}