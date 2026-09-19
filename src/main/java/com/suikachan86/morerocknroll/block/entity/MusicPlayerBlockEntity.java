package com.suikachan86.morerocknroll.block.entity;

import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackState;
import com.suikachan86.morerocknroll.track.TrackRef;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.Optional;

public final class MusicPlayerBlockEntity extends BlockEntity {
    private static final String TRACK_REF_KEY = "TrackRef";
    private static final String PLAYING_KEY = "Playing";
    private static final String PAUSED_KEY = "Paused";
    private static final String STARTED_AT_TICK_KEY = "StartedAtTick";
    private static final String POSITION_TICKS_KEY = "PositionTicks";

    private TrackRef selectedTrack;
    private MusicPlayerPlaybackState playbackState = MusicPlayerPlaybackState.STOPPED;
    private long startedAtTick = -1L;
    private long positionTicks;

    public MusicPlayerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUSIC_PLAYER, pos, state);
    }

    public Optional<TrackRef> selectedTrack() {
        return Optional.ofNullable(selectedTrack);
    }

    public boolean isPlaying() {
        return playbackState == MusicPlayerPlaybackState.PLAYING;
    }

    public boolean isPaused() {
        return playbackState == MusicPlayerPlaybackState.PAUSED;
    }

    public MusicPlayerPlaybackState playbackState() {
        return playbackState;
    }

    public long startedAtTick() {
        return startedAtTick;
    }

    public long positionTicks(long worldTime) {
        if (!isPlaying()) {
            return positionTicks;
        }
        return Math.max(0L, worldTime - startedAtTick);
    }

    public void selectTrack(TrackRef trackRef) {
        this.selectedTrack = Objects.requireNonNull(trackRef, "trackRef");
        markDirty();
    }

    public void start(TrackRef trackRef, long worldTime) {
        this.selectedTrack = Objects.requireNonNull(trackRef, "trackRef");
        this.playbackState = MusicPlayerPlaybackState.PLAYING;
        this.startedAtTick = worldTime;
        this.positionTicks = 0L;
        markDirty();
    }

    public long pause(long worldTime) {
        if (!isPlaying()) {
            return positionTicks;
        }

        this.positionTicks = positionTicks(worldTime);
        this.playbackState = MusicPlayerPlaybackState.PAUSED;
        this.startedAtTick = -1L;
        markDirty();
        return positionTicks;
    }

    public long resume(long worldTime) {
        if (!isPaused()) {
            return positionTicks;
        }

        this.playbackState = MusicPlayerPlaybackState.PLAYING;
        this.startedAtTick = worldTime - this.positionTicks;
        markDirty();
        return positionTicks;
    }

    public void stop() {
        this.playbackState = MusicPlayerPlaybackState.STOPPED;
        this.startedAtTick = -1L;
        this.positionTicks = 0L;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (selectedTrack != null) {
            nbt.putString(TRACK_REF_KEY, selectedTrack.serializedId());
        }
        nbt.putBoolean(PLAYING_KEY, isPlaying());
        nbt.putBoolean(PAUSED_KEY, isPaused());
        nbt.putLong(STARTED_AT_TICK_KEY, startedAtTick);
        long savedPositionTicks = positionTicks;
        if (isPlaying() && getWorld() != null) {
            savedPositionTicks = positionTicks(getWorld().getTime());
        }
        nbt.putLong(POSITION_TICKS_KEY, savedPositionTicks);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.selectedTrack = readTrackRef(nbt);
        boolean playing = selectedTrack != null && nbt.getBoolean(PLAYING_KEY);
        boolean paused = selectedTrack != null && !playing && nbt.getBoolean(PAUSED_KEY);
        this.playbackState = playing
                ? MusicPlayerPlaybackState.PLAYING
                : paused ? MusicPlayerPlaybackState.PAUSED : MusicPlayerPlaybackState.STOPPED;
        this.positionTicks = Math.max(0L, nbt.getLong(POSITION_TICKS_KEY));
        this.startedAtTick = this.playbackState == MusicPlayerPlaybackState.PLAYING
                ? nbt.getLong(STARTED_AT_TICK_KEY)
                : -1L;
    }

    private static TrackRef readTrackRef(NbtCompound nbt) {
        if (!nbt.contains(TRACK_REF_KEY, NbtElement.STRING_TYPE)) {
            return null;
        }

        try {
            return TrackRef.fromSerializedId(nbt.getString(TRACK_REF_KEY));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}