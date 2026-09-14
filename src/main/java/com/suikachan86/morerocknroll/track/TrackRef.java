package com.suikachan86.morerocknroll.track;

import java.util.Objects;
import java.util.UUID;

/**
 * Stable identity of a playable track.
 *
 * <p>The source is part of the identity so a local track can never be
 * confused with a built-in registry track that happens to have the same id.</p>
 */
public record TrackRef(Source source, String id) {
    public TrackRef {
        Objects.requireNonNull(source, "source");
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Track reference id must not be blank");
        }
    }

    public static TrackRef builtIn(String id) {
        return new TrackRef(Source.BUILT_IN, id);
    }

    public static TrackRef local(UUID id) {
        return new TrackRef(Source.LOCAL, Objects.requireNonNull(id, "id").toString());
    }

    /**
     * Stable form for block entity or config storage, for example
     * {@code builtin:time} or {@code local:550e8400-e29b-41d4-a716-446655440000}.
     */
    public String serializedId() {
        return source.serializedName + ":" + id;
    }

    public static TrackRef fromSerializedId(String serializedId) {
        Objects.requireNonNull(serializedId, "serializedId");
        int separator = serializedId.indexOf(':');
        if (separator <= 0 || separator == serializedId.length() - 1) {
            throw new IllegalArgumentException("Invalid track reference: " + serializedId);
        }

        String sourceName = serializedId.substring(0, separator);
        String id = serializedId.substring(separator + 1);
        return switch (sourceName) {
            case "builtin" -> builtIn(id);
            case "local" -> local(UUID.fromString(id));
            default -> throw new IllegalArgumentException("Unknown track source: " + sourceName);
        };
    }

    public enum Source {
        BUILT_IN("builtin"),
        LOCAL("local");

        private final String serializedName;

        Source(String serializedName) {
            this.serializedName = serializedName;
        }
    }
}
