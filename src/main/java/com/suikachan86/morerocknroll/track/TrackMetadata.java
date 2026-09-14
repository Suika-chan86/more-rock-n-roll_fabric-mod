package com.suikachan86.morerocknroll.track;

import net.minecraft.text.Text;

import java.util.Objects;

/**
 * UI-facing metadata shared by built-in and local tracks.
 */
public record TrackMetadata(
        TrackRef ref,
        Text displayName,
        float lengthSeconds
) {
    public TrackMetadata {
        Objects.requireNonNull(ref, "ref");
        Objects.requireNonNull(displayName, "displayName");
        if (!Float.isFinite(lengthSeconds) || lengthSeconds <= 0.0f) {
            throw new IllegalArgumentException("Track length must be positive");
        }
    }
}
