package com.fertility.config;

import com.fertility.Fertility;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

import java.util.List;

public class ClientConfigHandler {

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static IntValue overlayX;
    public static IntValue overlayY;
    public static BooleanValue showOverlay;
    public static BooleanValue autoHide;
    public static DoubleValue autoHideDelay;
    public static BooleanValue showOnBonemeal;
    public static BooleanValue showOnHoe;

    static {
        makeConfig();
    }

    private static void makeConfig() {
        CLIENT_BUILDER.comment("Client Settings").push(Fertility.MODID);
        showOverlay = CLIENT_BUILDER.comment("Should we show an overlay on the screen with crop icons?").define("ShowOverlay", true);
        overlayX = CLIENT_BUILDER.defineInRange("OverlayX", 0, 0, Integer.MAX_VALUE);
        overlayY = CLIENT_BUILDER.defineInRange("OverlayY", 0, 0, Integer.MAX_VALUE);
        autoHide = CLIENT_BUILDER.comment("Should the overlay be hidden after a certain delay of the crops changing/world loading?" +
                        "\nIf this is false, the overlay will always be visible")
                .define("AutoHideOverlay", true);
        autoHideDelay = CLIENT_BUILDER.comment("Time in seconds that the overlay is visible before hiding").defineInRange("AutoHideTime", 5, 0.1, Double.MAX_VALUE);
        showOnBonemeal = CLIENT_BUILDER.comment("Types of items which will force the overlay to be visible").define("Bonemeal", true);
        showOnHoe = CLIENT_BUILDER.define("Hoes", true);
        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

}
