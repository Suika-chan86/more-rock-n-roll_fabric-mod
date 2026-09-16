package com.suikachan86.morerocknroll;

import com.suikachan86.morerocknroll.client.MusicPlayerClientAudio;
import net.fabricmc.api.ClientModInitializer;

public class MoreRockNRollClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MusicPlayerClientAudio.initialize();
    }
}