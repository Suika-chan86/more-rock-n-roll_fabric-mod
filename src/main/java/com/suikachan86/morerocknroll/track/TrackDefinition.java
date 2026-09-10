package com.suikachan86.morerocknroll.track;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

import java.util.Objects;

/**
 * The data shared by every music disc in the mod.
 *
 * <p>This class deliberately stores data only. Registry objects are created by
 * the relevant registration classes.</p>
 */
public record TrackDefinition(
        String id,
        float lengthSeconds,
        int comparatorOutput,
        Rarity rarity
) {
    public TrackDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Track id must not be blank");
        }
        if (!Float.isFinite(lengthSeconds) || lengthSeconds <= 0.0f) {
            throw new IllegalArgumentException("Track length must be positive: " + id);
        }
        if (comparatorOutput < 0 || comparatorOutput > 15) {
            throw new IllegalArgumentException("Comparator output must be between 0 and 15: " + id);
        }
        Objects.requireNonNull(rarity, "rarity");
    }

    public Identifier soundId() {
        return MoreRockNRoll.id("music_disc." + id);
    }

    public Identifier itemId() {
        return MoreRockNRoll.id("music_disc_" + id);
    }

    public RegistryKey<JukeboxSong> jukeboxSongKey() {
        return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, MoreRockNRoll.id(id));
    }

    public String itemTranslationKey() {
        return Util.createTranslationKey("item", itemId());
    }

    public String jukeboxSongTranslationKey() {
        return Util.createTranslationKey("jukebox_song", MoreRockNRoll.id(id));
    }
}
