package com.suikachan86.morerocknroll.network;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record MusicPlayerPlaybackSnapshotPayload(
        BlockPos pos,
        Identifier soundId,
        int state,
        long positionTicks
) implements CustomPayload {
    public static final Id<MusicPlayerPlaybackSnapshotPayload> ID =
            new Id<>(MoreRockNRoll.id("music_player_playback_snapshot"));
    public static final PacketCodec<RegistryByteBuf, MusicPlayerPlaybackSnapshotPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, MusicPlayerPlaybackSnapshotPayload::pos,
            Identifier.PACKET_CODEC, MusicPlayerPlaybackSnapshotPayload::soundId,
            PacketCodecs.VAR_INT, MusicPlayerPlaybackSnapshotPayload::state,
            PacketCodecs.VAR_LONG, MusicPlayerPlaybackSnapshotPayload::positionTicks,
            MusicPlayerPlaybackSnapshotPayload::new
    );

    public MusicPlayerPlaybackSnapshotPayload {
        if (state < 0 || state > 2) {
            throw new IllegalArgumentException("Unknown music player state: " + state);
        }
        if (positionTicks < 0L) {
            throw new IllegalArgumentException("Music player position must not be negative");
        }
    }

    public MusicPlayerPlaybackState playbackState() {
        return MusicPlayerPlaybackState.fromId(state);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}