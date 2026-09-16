package com.suikachan86.morerocknroll.network;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record MusicPlayerPlaybackPayload(
        BlockPos pos,
        Identifier soundId,
        int action,
        long positionTicks
) implements CustomPayload {
    public static final Id<MusicPlayerPlaybackPayload> ID =
            new Id<>(MoreRockNRoll.id("music_player_playback"));
    public static final PacketCodec<RegistryByteBuf, MusicPlayerPlaybackPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, MusicPlayerPlaybackPayload::pos,
            Identifier.PACKET_CODEC, MusicPlayerPlaybackPayload::soundId,
            PacketCodecs.VAR_INT, MusicPlayerPlaybackPayload::action,
            PacketCodecs.VAR_LONG, MusicPlayerPlaybackPayload::positionTicks,
            MusicPlayerPlaybackPayload::new
    );

    public static final int START = 0;
    public static final int PAUSE = 1;
    public static final int RESUME = 2;
    public static final int STOP = 3;

    public MusicPlayerPlaybackPayload {
        if (action < START || action > STOP) {
            throw new IllegalArgumentException("Unknown music player action: " + action);
        }
        if (positionTicks < 0L) {
            throw new IllegalArgumentException("Music player position must not be negative");
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}