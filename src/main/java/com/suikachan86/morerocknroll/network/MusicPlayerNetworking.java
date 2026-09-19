package com.suikachan86.morerocknroll.network;

import com.suikachan86.morerocknroll.block.entity.MusicPlayerBlockEntity;
import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackState;
import com.suikachan86.morerocknroll.track.ModTracks;
import com.suikachan86.morerocknroll.track.TrackDefinition;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class MusicPlayerNetworking {
    private static final double BROADCAST_RADIUS = 64.0;

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(
                MusicPlayerPlaybackPayload.ID,
                MusicPlayerPlaybackPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                MusicPlayerPlaybackSnapshotPayload.ID,
                MusicPlayerPlaybackSnapshotPayload.CODEC
        );
    }

    public static void broadcast(
            World world,
            BlockPos pos,
            Identifier soundId,
            int action,
            long positionTicks
    ) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        MusicPlayerPlaybackPayload payload = new MusicPlayerPlaybackPayload(
                pos,
                soundId,
                action,
                positionTicks
        );
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            if (isWithinRange(player, pos)) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    public static void sendSnapshot(
            ServerPlayerEntity player,
            ServerWorld world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer
    ) {
        ServerPlayNetworking.send(player, snapshot(world, pos, musicPlayer));
    }

    public static void broadcastSnapshot(
            World world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer
    ) {
        if (!(world instanceof ServerWorld serverWorld)
                || musicPlayer.playbackState() == MusicPlayerPlaybackState.STOPPED) {
            return;
        }

        MusicPlayerPlaybackSnapshotPayload payload = snapshot(serverWorld, pos, musicPlayer);
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            if (isWithinRange(player, pos)) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    private static MusicPlayerPlaybackSnapshotPayload snapshot(
            ServerWorld world,
            BlockPos pos,
            MusicPlayerBlockEntity musicPlayer
    ) {
        TrackDefinition track = musicPlayer.selectedTrack()
                .flatMap(ModTracks::find)
                .orElseGet(ModTracks::first);
        return new MusicPlayerPlaybackSnapshotPayload(
                pos,
                track.soundId(),
                musicPlayer.playbackState().id(),
                musicPlayer.positionTicks(world.getTime())
        );
    }

    private static boolean isWithinRange(ServerPlayerEntity player, BlockPos pos) {
        Vec3d center = Vec3d.ofCenter(pos);
        double maxDistanceSquared = BROADCAST_RADIUS * BROADCAST_RADIUS;
        return player.getPos().squaredDistanceTo(center) <= maxDistanceSquared;
    }

    private MusicPlayerNetworking() {
    }
}