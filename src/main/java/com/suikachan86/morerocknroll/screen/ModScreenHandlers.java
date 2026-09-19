package com.suikachan86.morerocknroll.screen;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public final class ModScreenHandlers {
    public static final ScreenHandlerType<MusicPlayerScreenHandler> MUSIC_PLAYER = Registry.register(
            Registries.SCREEN_HANDLER,
            MoreRockNRoll.id("music_player"),
            new ScreenHandlerType<>(MusicPlayerScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );

    private ModScreenHandlers() {
    }

    public static void initialize() {
        // Loads the class so the static registration runs during mod initialization.
    }
}