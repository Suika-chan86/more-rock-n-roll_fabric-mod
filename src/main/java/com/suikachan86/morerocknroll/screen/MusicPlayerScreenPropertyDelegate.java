package com.suikachan86.morerocknroll.screen;

import com.suikachan86.morerocknroll.block.entity.MusicPlayerBlockEntity;
import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackState;
import com.suikachan86.morerocknroll.track.ModTracks;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class MusicPlayerScreenPropertyDelegate implements PropertyDelegate {
    public static final int TRACK_INDEX = 0;
    public static final int PLAYBACK_STATE = 1;
    public static final int POSITION_TICKS = 2;
    public static final int POS_X = 3;
    public static final int POS_Y = 4;
    public static final int POS_Z = 5;
    public static final int POSITION_READY = 6;
    public static final int SIZE = 7;

    private final World world;
    private final BlockPos pos;
    private final int[] clientValues = {
            -1,
            MusicPlayerPlaybackState.STOPPED.id(),
            0,
            0,
            0,
            0,
            0
    };

    public MusicPlayerScreenPropertyDelegate(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @Override
    public int get(int index) {
        if (world == null || pos == null) {
            return clientValues[index];
        }

        if (!(world.getBlockEntity(pos) instanceof MusicPlayerBlockEntity musicPlayer)) {
            return switch (index) {
                case TRACK_INDEX -> -1;
                case PLAYBACK_STATE -> MusicPlayerPlaybackState.STOPPED.id();
                case POSITION_TICKS, POS_X, POS_Y, POS_Z, POSITION_READY -> 0;
                default -> throw new IndexOutOfBoundsException("Unknown music player property: " + index);
            };
        }

        return switch (index) {
            case TRACK_INDEX -> musicPlayer.selectedTrack()
                    .flatMap(ModTracks::find)
                    .map(ModTracks.ALL::indexOf)
                    .orElse(-1);
            case PLAYBACK_STATE -> musicPlayer.playbackState().id();
            case POSITION_TICKS -> positionTicks(musicPlayer);
            case POS_X -> pos.getX();
            case POS_Y -> pos.getY();
            case POS_Z -> pos.getZ();
            case POSITION_READY -> 1;
            default -> throw new IndexOutOfBoundsException("Unknown music player property: " + index);
        };
    }

    @Override
    public void set(int index, int value) {
        clientValues[index] = value;
    }

    @Override
    public int size() {
        return SIZE;
    }

    private int positionTicks(MusicPlayerBlockEntity musicPlayer) {
        long positionTicks = musicPlayer.positionTicks(world.getTime());
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, positionTicks));
    }
}