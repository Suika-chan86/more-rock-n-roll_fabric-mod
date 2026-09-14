package com.suikachan86.morerocknroll.track;

import java.util.Optional;

/**
 * Playback boundary used by the player logic.
 *
 * <p>Implementations decide how a track is played. A built-in implementation
 * may use Minecraft sound events, while a future local implementation may use
 * a client-side audio source. The UI and block entity do not depend on either
 * implementation.</p>
 */
public interface PlaybackBackend {
    Optional<PlaybackSession> play(TrackRef ref);
}
