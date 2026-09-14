package com.suikachan86.morerocknroll.track;

import java.util.List;
import java.util.Optional;

/**
 * Source-independent catalogue used by the player UI and controller.
 */
public interface TrackCatalog {
    List<TrackMetadata> tracks();

    Optional<TrackMetadata> find(TrackRef ref);
}
