package com.suikachan86.morerocknroll.playback;

public enum MusicPlayerPlaybackState {
    STOPPED(0),
    PLAYING(1),
    PAUSED(2);

    private final int id;

    MusicPlayerPlaybackState(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static MusicPlayerPlaybackState fromId(int id) {
        return switch (id) {
            case 1 -> PLAYING;
            case 2 -> PAUSED;
            default -> STOPPED;
        };
    }
}