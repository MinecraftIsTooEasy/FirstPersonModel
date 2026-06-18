package vbonedra.first_person_model.config;

import fi.dy.masa.malilib.config.ConfigTab;
import fi.dy.masa.malilib.config.SimpleConfigs;
import fi.dy.masa.malilib.config.options.*;

import java.util.ArrayList;
import java.util.List;

public class FPMConfigs extends SimpleConfigs {
    public static final ConfigBoolean RenderFirstPersonModel = new ConfigBoolean("Render First Person Model", true);
    public static final ConfigBoolean RenderHudHandsInFirstPersonModel = new ConfigBoolean("Render Hud Hands In First Person Model", false);
    public static final ConfigDouble HeadOffset = new ConfigDouble("Head Offset", 4, -10, 10);

    private static final FPMConfigs Instance;
    public static final List<ConfigBase<?>> MainBase;
    public static final List<ConfigBase<?>> Total;
    public static final List<ConfigTab> tabs;

    public FPMConfigs(String name, List<ConfigHotkey> hotkeys, List<ConfigBase<?>> values) {
        super(name, hotkeys, values);
    }

    public List<ConfigTab> getConfigTabs() {
        return tabs;
    }

    public static FPMConfigs getInstance() {
        return Instance;
    }

    static {
        Total = new ArrayList<>();
        tabs = new ArrayList<>();
        MainBase = List.of(
                RenderFirstPersonModel,
                RenderHudHandsInFirstPersonModel,
                HeadOffset
        );
        Total.addAll(MainBase);
        tabs.add(new ConfigTab("First Person Model", MainBase));
        Instance = new FPMConfigs("First Person Model", null, Total);
    }
}