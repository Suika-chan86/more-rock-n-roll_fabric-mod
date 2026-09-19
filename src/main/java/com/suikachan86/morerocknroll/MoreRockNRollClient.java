package com.suikachan86.morerocknroll;

import com.suikachan86.morerocknroll.client.MusicPlayerClientAudio;
import com.suikachan86.morerocknroll.client.MusicPlayerScreen;
import com.suikachan86.morerocknroll.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class MoreRockNRollClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MusicPlayerClientAudio.initialize();
        HandledScreens.register(ModScreenHandlers.MUSIC_PLAYER, MusicPlayerScreen::new);
    }
}