package com.suikachan86.morerocknroll.track;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adapter that exposes the existing built-in track definitions through the
 * source-independent catalogue API.
 */
public final class BuiltInTrackCatalog implements TrackCatalog {
    private final List<TrackMetadata> tracks;
    private final Map<TrackRef, TrackMetadata> tracksByRef;

    private BuiltInTrackCatalog(Collection<TrackDefinition> definitions) {
        Map<TrackRef, TrackMetadata> metadataByRef = new LinkedHashMap<>();
        for (TrackDefinition definition : definitions) {
            TrackRef ref = definition.ref();
            TrackMetadata metadata = new TrackMetadata(
                    ref,
                    Text.translatable(definition.jukeboxSongTranslationKey()),
                    definition.lengthSeconds()
            );
            if (metadataByRef.putIfAbsent(ref, metadata) != null) {
                throw new IllegalArgumentException("Duplicate track reference: " + ref.serializedId());
            }
        }

        this.tracks = List.copyOf(new ArrayList<>(metadataByRef.values()));
        this.tracksByRef = Map.copyOf(metadataByRef);
    }

    public static BuiltInTrackCatalog from(Collection<TrackDefinition> definitions) {
        return new BuiltInTrackCatalog(definitions);
    }

    @Override
    public List<TrackMetadata> tracks() {
        return tracks;
    }

    @Override
    public Optional<TrackMetadata> find(TrackRef ref) {
        return Optional.ofNullable(tracksByRef.get(ref));
    }
}
