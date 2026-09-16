package com.suikachan86.morerocknroll.playback;

import com.suikachan86.morerocknroll.block.entity.MusicPlayerBlockEntity;
import com.suikachan86.morerocknroll.network.MusicPlayerNetworking;
import com.suikachan86.morerocknroll.network.MusicPlayerPlaybackPayload;
import com.suikachan86.morerocknroll.track.ModTracks;
import com.suikachan86.morerocknroll.track.TrackDefinition;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class MusicPlayerPlaybackController {
    public static void handleInteraction(
            World world,
            BlockPos pos,
            PlayerEntity player,
            MusicPlayerBlockEntity musicPlayer
    ) {
        TrackDefinition track = musicPlayer.selectedTrack()
                .flatMap(ModTracks::find)
                .orElseGet(ModTracks::first);

        if (player.isSneaking()) {
            if (musicPlayer.isPlaying() || musicPlayer.isPaused()) {
                stop(world, pos, musicPlayer, track);
            }
        } else if (musicPlayer.isPlaying()) {
            pause(world, pos, musicPlayer, track);
        } else if (musicPlayer.isPaused()) {
            resume(world, pos, musicPlayer, track);
        } else {
            start(world, pos, musicPlayer, track);
        }
    }

    public static void tick(
            World world,
            BlockPos pos,
            BlockState state,
            MusicPlayerBlockEntity musicPlayer
    ) {
        if (world.isClient || !musicPlayer.isPlaying()) {
            return;
        }

        TrackDefinition currentTrack = musicPlayer.selectedTrack()
                .flatMap(ModTracks::find)
                .orElse(null);
        if (currentTrack == null) {
            musicPlayer.stop();
            return;
        }

        if (musicPlayer.positionTicks(world.getTime()) < currentTrack.lengthTicks()) {
            return;
        }

        start(world, pos, musicPlayer, ModTracks.next(currentTrack.ref()));
    }

    private static void start(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer,
            TrackDefinition track
    ) {
        musicPlayer.start(track.ref(), world.getTime());
        MusicPlayerNetworking.broadcast(
                world,
                pos,
                track.soundId(),
                MusicPlayerPlaybackPayload.START,
                0L
        );
    }

    private static void pause(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer,
            TrackDefinition track
    ) {
        long positionTicks = musicPlayer.pause(world.getTime());
        MusicPlayerNetworking.broadcast(
                world,
                pos,
                track.soundId(),
                MusicPlayerPlaybackPayload.PAUSE,
                positionTicks
        );
    }

    private static void resume(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer,
            TrackDefinition track
    ) {
        long positionTicks = musicPlayer.resume(world.getTime());
        MusicPlayerNetworking.broadcast(
                world,
                pos,
                track.soundId(),
                MusicPlayerPlaybackPayload.RESUME,
                positionTicks
        );
    }

    private static void stop(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer,
            TrackDefinition track
    ) {
        musicPlayer.stop();
        MusicPlayerNetworking.broadcast(
                world,
                pos,
                track.soundId(),
                MusicPlayerPlaybackPayload.STOP,
                0L
        );
    }

    private MusicPlayerPlaybackController() {
    }
}