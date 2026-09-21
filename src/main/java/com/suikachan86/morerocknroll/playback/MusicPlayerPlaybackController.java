package com.suikachan86.morerocknroll.playback;

import com.suikachan86.morerocknroll.block.entity.MusicPlayerBlockEntity;
import com.suikachan86.morerocknroll.network.MusicPlayerNetworking;
import com.suikachan86.morerocknroll.network.MusicPlayerPlaybackPayload;
import com.suikachan86.morerocknroll.track.ModTracks;
import com.suikachan86.morerocknroll.track.TrackDefinition;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
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
                stop(world, pos, musicPlayer);
            }
        } else if (musicPlayer.isPlaying()) {
            pause(world, pos, musicPlayer);
        } else if (musicPlayer.isPaused()) {
            resume(world, pos, musicPlayer);
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
        if (world.isClient) {
            return;
        }

        if (musicPlayer.isPaused()) {
            if (world.getTime() % 20L == 0L) {
                MusicPlayerNetworking.broadcastSnapshot(world, pos, musicPlayer);
            }
            return;
        }

        if (!musicPlayer.isPlaying()) {
            return;
        }

        TrackDefinition currentTrack = musicPlayer.selectedTrack()
                .flatMap(ModTracks::find)
                .orElse(null);
        if (currentTrack == null) {
            musicPlayer.stop();
            return;
        }

        long positionTicks = musicPlayer.positionTicks(world.getTime());
        if (positionTicks >= currentTrack.lengthTicks()) {
            start(world, pos, musicPlayer, ModTracks.next(currentTrack.ref()));
            return;
        }

        if (positionTicks > 0L && positionTicks % 20L == 0L) {
            spawnNoteParticle(world, pos);
        }

        if (world.getTime() % 20L == 0L) {
            MusicPlayerNetworking.broadcastSnapshot(world, pos, musicPlayer);
        }
    }

    private static void start(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer,
            TrackDefinition track
    ) {
        musicPlayer.start(track.ref(), world.getTime());
        spawnNoteParticle(world, pos);
        MusicPlayerNetworking.broadcast(
                world,
                pos,
                track.soundId(),
                MusicPlayerPlaybackPayload.START,
                0L
        );
    }

    public static void pause(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer
    ) {
        if (!musicPlayer.isPlaying()) {
            return;
        }

        TrackDefinition track = selectedTrack(musicPlayer);
        long positionTicks = musicPlayer.pause(world.getTime());
        MusicPlayerNetworking.broadcast(
                world,
                pos,
                track.soundId(),
                MusicPlayerPlaybackPayload.PAUSE,
                positionTicks
        );
    }

    public static void resume(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer
    ) {
        if (!musicPlayer.isPaused()) {
            return;
        }

        TrackDefinition track = selectedTrack(musicPlayer);
        long positionTicks = musicPlayer.resume(world.getTime());
        MusicPlayerNetworking.broadcast(
                world,
                pos,
                track.soundId(),
                MusicPlayerPlaybackPayload.RESUME,
                positionTicks
        );
    }

    public static void stop(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer
    ) {
        if (!musicPlayer.isPlaying() && !musicPlayer.isPaused()) {
            return;
        }

        TrackDefinition track = selectedTrack(musicPlayer);
        musicPlayer.stop();
        MusicPlayerNetworking.broadcast(
                world,
                pos,
                track.soundId(),
                MusicPlayerPlaybackPayload.STOP,
                0L
        );
    }

    public static void selectTrack(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer,
            TrackDefinition track
    ) {
        start(world, pos, musicPlayer, track);
    }

    private static void spawnNoteParticle(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        serverWorld.spawnParticles(
                ParticleTypes.NOTE,
                pos.getX() + 0.5,
                pos.getY() + 1.2,
                pos.getZ() + 0.5,
                1,
                0.0,
                world.getRandom().nextInt(4) / 24.0,
                0.0,
                1.0
        );
    }
    private static TrackDefinition selectedTrack(MusicPlayerBlockEntity musicPlayer) {
        return musicPlayer.selectedTrack()
                .flatMap(ModTracks::find)
                .orElseGet(ModTracks::first);
    }

    private MusicPlayerPlaybackController() {
    }
}