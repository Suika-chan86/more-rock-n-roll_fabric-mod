package com.suikachan86.morerocknroll.sound;

import com.suikachan86.morerocknroll.track.ModTracks;
import com.suikachan86.morerocknroll.track.TrackDefinition;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModSoundEvents {
    private static final Map<String, RegistryEntry.Reference<SoundEvent>> SOUND_EVENTS = registerAll();

    private static Map<String, RegistryEntry.Reference<SoundEvent>> registerAll() {
        Map<String, RegistryEntry.Reference<SoundEvent>> soundEvents = new LinkedHashMap<>();
        for (TrackDefinition track : ModTracks.ALL) {
            soundEvents.put(track.id(), registerReference(track.soundId()));
        }
        return Map.copyOf(soundEvents);
    }

    private static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id) {
        return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static RegistryEntry.Reference<SoundEvent> get(TrackDefinition track) {
        RegistryEntry.Reference<SoundEvent> sound = SOUND_EVENTS.get(track.id());
        if (sound == null) {
            throw new IllegalArgumentException("Unknown track: " + track.id());
        }
        return sound;
    }

    public static void initialize() {
        // 空方法，触发类加载
    }
}

