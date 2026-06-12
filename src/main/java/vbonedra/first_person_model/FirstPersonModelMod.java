package vbonedra.first_person_model;

import fi.dy.masa.malilib.config.ConfigManager;
import vbonedra.first_person_model.config.FPMConfigs;
import vbonedra.first_person_model.event.FirstPersonModelEvent;
import net.fabricmc.api.ModInitializer;

import net.xiaoyu233.fml.ModResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FirstPersonModelMod implements ModInitializer {
    public static final String MOD_ID = "first_person_model";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModResourceManager.addResourcePackDomain(MOD_ID);

        FPMConfigs.getInstance().load();
        ConfigManager.getInstance().registerConfig(FPMConfigs.getInstance());

        FirstPersonModelEvent.register();
    }
}
