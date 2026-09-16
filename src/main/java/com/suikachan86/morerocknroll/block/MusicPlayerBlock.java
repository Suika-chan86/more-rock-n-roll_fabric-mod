package com.suikachan86.morerocknroll.block;

import com.mojang.serialization.MapCodec;
import com.suikachan86.morerocknroll.block.entity.ModBlockEntities;
import com.suikachan86.morerocknroll.block.entity.MusicPlayerBlockEntity;
import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackController;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class MusicPlayerBlock extends BlockWithEntity {
    public static final MapCodec<MusicPlayerBlock> CODEC = createCodec(MusicPlayerBlock::new);

    public MusicPlayerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MusicPlayerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return world.isClient
                ? null
                : validateTicker(type, ModBlockEntities.MUSIC_PLAYER, MusicPlayerPlaybackController::tick);
    }

    @Override
    protected ActionResult onUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            BlockHitResult hit
    ) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof MusicPlayerBlockEntity musicPlayer)) {
            return ActionResult.PASS;
        }

        MusicPlayerPlaybackController.handleInteraction(world, pos, player, musicPlayer);
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}