package com.suikachan86.morerocknroll.client;

import com.suikachan86.morerocknroll.mixin.SoundManagerAccessor;
import com.suikachan86.morerocknroll.mixin.SoundSystemAccessor;
import com.suikachan86.morerocknroll.network.MusicPlayerPlaybackPayload;
import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackState;
import com.suikachan86.morerocknroll.network.MusicPlayerPlaybackSnapshotPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public final class MusicPlayerClientAudio {
    private static final Map<BlockPos, PositionedSoundInstance> ACTIVE_SOUNDS = new HashMap<>();
    private static final Map<BlockPos, Integer> PENDING_ACTIONS = new HashMap<>();

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(
                MusicPlayerPlaybackPayload.ID,
                (payload, context) -> context.client().execute(() -> apply(payload))
        );
        ClientPlayNetworking.registerGlobalReceiver(
                MusicPlayerPlaybackSnapshotPayload.ID,
                (payload, context) -> context.client().execute(() -> apply(payload))
        );
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clear());
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
    }

    private static void apply(MusicPlayerPlaybackPayload payload) {
        switch (payload.action()) {
            case MusicPlayerPlaybackPayload.START -> start(payload);
            case MusicPlayerPlaybackPayload.PAUSE -> pause(payload.pos());
            case MusicPlayerPlaybackPayload.RESUME -> resume(payload);
            case MusicPlayerPlaybackPayload.STOP -> stop(payload.pos());
            default -> throw new IllegalStateException("Unknown music player action: " + payload.action());
        }
    }

    private static void apply(MusicPlayerPlaybackSnapshotPayload payload) {
        switch (payload.playbackState()) {
            case STOPPED -> stop(payload.pos());
            case PLAYING -> syncPlaying(payload);
            case PAUSED -> syncPaused(payload);
        }
    }

    private static void syncPlaying(MusicPlayerPlaybackSnapshotPayload payload) {
        PositionedSoundInstance sound = ACTIVE_SOUNDS.get(payload.pos());
        if (sound == null
                || !payload.soundId().equals(sound.getId())
                || !hasLiveSource(sound)) {
            start(new MusicPlayerPlaybackPayload(
                    payload.pos(),
                    payload.soundId(),
                    MusicPlayerPlaybackPayload.START,
                    payload.positionTicks()
            ));
            return;
        }

        PENDING_ACTIONS.remove(payload.pos());
        if (!runOnSource(sound, source -> source.resume())) {
            PENDING_ACTIONS.put(payload.pos(), MusicPlayerPlaybackPayload.RESUME);
        }
    }

    private static void syncPaused(MusicPlayerPlaybackSnapshotPayload payload) {
        PositionedSoundInstance sound = ACTIVE_SOUNDS.get(payload.pos());
        if (sound == null || !payload.soundId().equals(sound.getId())) {
            start(new MusicPlayerPlaybackPayload(
                    payload.pos(),
                    payload.soundId(),
                    MusicPlayerPlaybackPayload.START,
                    payload.positionTicks()
            ));
            pause(payload.pos());
            return;
        }

        pause(payload.pos());
    }

    private static void start(MusicPlayerPlaybackPayload payload) {
        stop(payload.pos());

        BlockPos pos = payload.pos();
        PositionedSoundInstance sound = new PositionedSoundInstance(
                payload.soundId(),
                SoundCategory.RECORDS,
                1.0f,
                1.0f,
                Random.create(),
                false,
                0,
                SoundInstance.AttenuationType.LINEAR,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                false
        );
        ACTIVE_SOUNDS.put(pos, sound);
        MinecraftClient.getInstance().getSoundManager().play(sound);
    }

    public static void pauseLocally(BlockPos pos) {
        pause(pos);
    }

    private static void pause(BlockPos pos) {
        PositionedSoundInstance sound = ACTIVE_SOUNDS.get(pos);
        if (sound == null || !runOnSource(sound, source -> source.pause())) {
            PENDING_ACTIONS.put(pos, MusicPlayerPlaybackPayload.PAUSE);
        }
    }

    private static void resume(MusicPlayerPlaybackPayload payload) {
        PositionedSoundInstance sound = ACTIVE_SOUNDS.get(payload.pos());
        if (sound == null) {
            start(payload);
            return;
        }
        if (!runOnSource(sound, source -> source.resume())) {
            PENDING_ACTIONS.put(payload.pos(), MusicPlayerPlaybackPayload.RESUME);
        }
    }

    private static void stop(BlockPos pos) {
        PENDING_ACTIONS.remove(pos);
        PositionedSoundInstance sound = ACTIVE_SOUNDS.remove(pos);
        if (sound != null) {
            MinecraftClient.getInstance().getSoundManager().stop(sound);
        }
    }

    private static void clear() {
        for (PositionedSoundInstance sound : ACTIVE_SOUNDS.values()) {
            MinecraftClient.getInstance().getSoundManager().stop(sound);
        }
        ACTIVE_SOUNDS.clear();
        PENDING_ACTIONS.clear();
    }

    private static void tick() {
        cleanupStoppedSounds();

        Iterator<Map.Entry<BlockPos, Integer>> iterator = PENDING_ACTIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = iterator.next();
            PositionedSoundInstance sound = ACTIVE_SOUNDS.get(entry.getKey());
            if (sound == null) {
                iterator.remove();
                continue;
            }

            Channel.SourceManager sourceManager = sourceManagerFor(sound);
            if (sourceManager == null) {
                continue;
            }
            if (sourceManager.isStopped()) {
                ACTIVE_SOUNDS.remove(entry.getKey());
                iterator.remove();
                continue;
            }

            if (entry.getValue() == MusicPlayerPlaybackPayload.PAUSE) {
                sourceManager.run(source -> source.pause());
            } else {
                sourceManager.run(source -> source.resume());
            }
            iterator.remove();
        }
    }

    private static void cleanupStoppedSounds() {
        Iterator<Map.Entry<BlockPos, PositionedSoundInstance>> iterator = ACTIVE_SOUNDS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, PositionedSoundInstance> entry = iterator.next();
            Channel.SourceManager sourceManager = sourceManagerFor(entry.getValue());
            if (sourceManager != null && sourceManager.isStopped()) {
                PENDING_ACTIONS.remove(entry.getKey());
                iterator.remove();
            }
        }
    }

    private static boolean hasLiveSource(SoundInstance sound) {
        Channel.SourceManager sourceManager = sourceManagerFor(sound);
        return sourceManager != null && !sourceManager.isStopped();
    }

    private static boolean runOnSource(SoundInstance sound, Consumer<net.minecraft.client.sound.Source> action) {
        Channel.SourceManager sourceManager = sourceManagerFor(sound);
        if (sourceManager == null || sourceManager.isStopped()) {
            return false;
        }

        sourceManager.run(action);
        return true;
    }

    private static Channel.SourceManager sourceManagerFor(SoundInstance sound) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        SoundSystem soundSystem = ((SoundManagerAccessor) soundManager).moreRockNRoll$getSoundSystem();
        return ((SoundSystemAccessor) soundSystem)
                .moreRockNRoll$getSources()
                .get(sound);
    }

    private MusicPlayerClientAudio() {
    }
}