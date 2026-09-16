package com.suikachan86.morerocknroll.block.entity;

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
    private boolean playing;
    private boolean paused;
    private long startedAtTick = -1L;
    private long positionTicks;

    public MusicPlayerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUSIC_PLAYER, pos, state);
    }

    public Optional<TrackRef> selectedTrack() {
        return Optional.ofNullable(selectedTrack);
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isPaused() {
        return paused;
    }

    public long startedAtTick() {
        return startedAtTick;
    }

    public long positionTicks(long worldTime) {
        if (!playing) {
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
        this.playing = true;
        this.paused = false;
        this.startedAtTick = worldTime;
        this.positionTicks = 0L;
        markDirty();
    }

    public long pause(long worldTime) {
        if (!playing) {
            return positionTicks;
        }

        this.positionTicks = positionTicks(worldTime);
        this.playing = false;
        this.paused = true;
        this.startedAtTick = -1L;
        markDirty();
        return positionTicks;
    }

    public long resume(long worldTime) {
        if (!paused) {
            return positionTicks;
        }

        this.playing = true;
        this.paused = false;
        this.startedAtTick = worldTime - this.positionTicks;
        markDirty();
        return positionTicks;
    }

    public void stop() {
        this.playing = false;
        this.paused = false;
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
        nbt.putBoolean(PLAYING_KEY, playing);
        nbt.putBoolean(PAUSED_KEY, paused);
        nbt.putLong(STARTED_AT_TICK_KEY, startedAtTick);
        long savedPositionTicks = positionTicks;
        if (playing && getWorld() != null) {
            savedPositionTicks = positionTicks(getWorld().getTime());
        }
        nbt.putLong(POSITION_TICKS_KEY, savedPositionTicks);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.selectedTrack = readTrackRef(nbt);
        this.playing = selectedTrack != null && nbt.getBoolean(PLAYING_KEY);
        this.paused = selectedTrack != null && !playing && nbt.getBoolean(PAUSED_KEY);
        this.positionTicks = Math.max(0L, nbt.getLong(POSITION_TICKS_KEY));
        this.startedAtTick = playing ? nbt.getLong(STARTED_AT_TICK_KEY) : -1L;
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