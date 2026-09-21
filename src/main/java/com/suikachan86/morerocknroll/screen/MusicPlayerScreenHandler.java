package com.suikachan86.morerocknroll.screen;

import com.suikachan86.morerocknroll.block.ModBlocks;
import com.suikachan86.morerocknroll.block.entity.MusicPlayerBlockEntity;
import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackController;
import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackState;
import com.suikachan86.morerocknroll.track.ModTracks;
import com.suikachan86.morerocknroll.track.TrackDefinition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public final class MusicPlayerScreenHandler extends ScreenHandler {
    public static final int PAUSE_BUTTON = 100;
    public static final int RESUME_BUTTON = 101;
    public static final int STOP_BUTTON = 102;

    private final World world;
    private final BlockPos pos;
    private final PropertyDelegate state;

    public MusicPlayerScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, null, null);
    }

    public MusicPlayerScreenHandler(
            int syncId,
            PlayerInventory inventory,
            World world,
            BlockPos pos
    ) {
        super(ModScreenHandlers.MUSIC_PLAYER, syncId);
        this.world = world;
        this.pos = pos;
        this.state = new MusicPlayerScreenPropertyDelegate(world, pos);
        addProperties(state);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (world == null || pos == null || !canUse(player)) {
            return false;
        }

        if (!(world.getBlockEntity(pos) instanceof MusicPlayerBlockEntity musicPlayer)) {
            return false;
        }

        if (id >= 0 && id < ModTracks.ALL.size()) {
            TrackDefinition track = ModTracks.ALL.get(id);
            MusicPlayerPlaybackController.selectTrack(world, pos, musicPlayer, track);
            return true;
        }

        switch (id) {
            case PAUSE_BUTTON -> {
                if (!canPause()) {
                    return false;
                }
                MusicPlayerPlaybackController.pause(world, pos, musicPlayer);
            }
            case RESUME_BUTTON -> {
                if (!canResume()) {
                    return false;
                }
                MusicPlayerPlaybackController.resume(world, pos, musicPlayer);
            }
            case STOP_BUTTON -> {
                if (!canStop()) {
                    return false;
                }
                MusicPlayerPlaybackController.stop(world, pos, musicPlayer);
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    public int currentTrackIndex() {
        return state.get(MusicPlayerScreenPropertyDelegate.TRACK_INDEX);
    }

    public MusicPlayerPlaybackState playbackState() {
        return MusicPlayerPlaybackState.fromId(
                state.get(MusicPlayerScreenPropertyDelegate.PLAYBACK_STATE)
        );
    }

    public int positionTicks() {
        return state.get(MusicPlayerScreenPropertyDelegate.POSITION_TICKS);
    }

    public Optional<BlockPos> blockPos() {
        if (state.get(MusicPlayerScreenPropertyDelegate.POSITION_READY) == 0) {
            return Optional.empty();
        }
        return Optional.of(new BlockPos(
                state.get(MusicPlayerScreenPropertyDelegate.POS_X),
                state.get(MusicPlayerScreenPropertyDelegate.POS_Y),
                state.get(MusicPlayerScreenPropertyDelegate.POS_Z)
        ));
    }

    public boolean canPause() {
        return playbackState() == MusicPlayerPlaybackState.PLAYING;
    }

    public boolean canResume() {
        return playbackState() == MusicPlayerPlaybackState.PAUSED;
    }

    public boolean canStop() {
        return playbackState() != MusicPlayerPlaybackState.STOPPED;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        if (world == null || pos == null) {
            return true;
        }
        return ScreenHandler.canUse(
                ScreenHandlerContext.create(world, pos),
                player,
                ModBlocks.MUSIC_PLAYER
        );
    }
}