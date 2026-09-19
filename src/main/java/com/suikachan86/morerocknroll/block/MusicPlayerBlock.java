package com.suikachan86.morerocknroll.block;

import com.mojang.serialization.MapCodec;
import com.suikachan86.morerocknroll.block.entity.ModBlockEntities;
import com.suikachan86.morerocknroll.block.entity.MusicPlayerBlockEntity;
import com.suikachan86.morerocknroll.item.ModItems;
import com.suikachan86.morerocknroll.network.MusicPlayerNetworking;
import com.suikachan86.morerocknroll.playback.MusicPlayerPlaybackController;
import com.suikachan86.morerocknroll.screen.MusicPlayerScreenHandler;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
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
    protected ItemActionResult onUseWithItem(
            ItemStack stack,
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit
    ) {
        if (ModItems.findTrack(stack.getItem()).isEmpty()) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (world.isClient) {
            return ItemActionResult.SUCCESS;
        }

        NamedScreenHandlerFactory factory = createScreenHandlerFactory(state, world, pos);
        if (factory == null || player.openHandledScreen(factory).isEmpty()) {
            return ItemActionResult.FAIL;
        }
        if (player instanceof ServerPlayerEntity serverPlayer
                && world instanceof ServerWorld serverWorld
                && world.getBlockEntity(pos) instanceof MusicPlayerBlockEntity musicPlayer) {
            MusicPlayerNetworking.sendSnapshot(serverPlayer, serverWorld, pos, musicPlayer);
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(
            BlockState state,
            World world,
            BlockPos pos
    ) {
        if (!(world.getBlockEntity(pos) instanceof MusicPlayerBlockEntity)) {
            return null;
        }

        return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) ->
                        new MusicPlayerScreenHandler(syncId, inventory, world, pos),
                Text.translatable("block.more-rock-n-roll.music_player")
        );
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