package com.suikachan86.morerocknroll.network;

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
        Vec3d center = Vec3d.ofCenter(pos);
        double maxDistanceSquared = BROADCAST_RADIUS * BROADCAST_RADIUS;
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            if (player.getPos().squaredDistanceTo(center) <= maxDistanceSquared) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    private MusicPlayerNetworking() {
    }
}