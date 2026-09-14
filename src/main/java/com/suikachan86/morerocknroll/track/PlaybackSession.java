package com.suikachan86.morerocknroll.track;

/**
 * Controls one active playback session.
 */
public interface PlaybackSession {
    void pause();

    void resume();

    void stop();

    boolean isPlaying();

    float progressSeconds();
}
