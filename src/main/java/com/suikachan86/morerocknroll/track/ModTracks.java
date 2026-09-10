package com.suikachan86.morerocknroll.track;

import net.minecraft.util.Rarity;

import java.util.List;

/**
 * The single source of truth for the mod's music-disc catalogue.
 */
public final class ModTracks {
    public static final List<TrackDefinition> ALL = List.of(
            new TrackDefinition("nanmonee", 171.0f, 15, Rarity.EPIC),
            new TrackDefinition("kakumei", 98.0f, 15, Rarity.EPIC),
            new TrackDefinition("time", 413.0f, 14, Rarity.EPIC),
            new TrackDefinition("homelandii", 213.0f, 15, Rarity.EPIC),
            new TrackDefinition("in_the_aeroplane_over_the_sea", 202.0f, 14, Rarity.EPIC),
            new TrackDefinition("summer68", 329.0f, 14, Rarity.EPIC),
            new TrackDefinition("siberian_khatru", 535.0f, 14, Rarity.EPIC),
            new TrackDefinition("dancing_with_my_own_shadow", 360.0f, 15, Rarity.EPIC)
    );

    private ModTracks() {
    }
}
