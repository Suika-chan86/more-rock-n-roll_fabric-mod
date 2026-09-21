package com.suikachan86.morerocknroll.block;

import com.mojang.serialization.MapCodec;
import com.suikachan86.morerocknroll.block.entity.MusicDiscShelfBlockEntity;
import com.suikachan86.morerocknroll.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class MusicDiscShelfBlock extends BlockWithEntity {
    public static final MapCodec<MusicDiscShelfBlock> CODEC = createCodec(MusicDiscShelfBlock::new);
    public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of(
            Properties.SLOT_0_OCCUPIED,
            Properties.SLOT_1_OCCUPIED,
            Properties.SLOT_2_OCCUPIED,
            Properties.SLOT_3_OCCUPIED,
            Properties.SLOT_4_OCCUPIED,
            Properties.SLOT_5_OCCUPIED
    );

    public MusicDiscShelfBlock(Settings settings) {
        super(settings);

        BlockState defaultState = getStateManager()
                .getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH);
        for (BooleanProperty property : SLOT_OCCUPIED_PROPERTIES) {
            defaultState = defaultState.with(property, false);
        }
        setDefaultState(defaultState);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
        for (BooleanProperty property : SLOT_OCCUPIED_PROPERTIES) {
            builder.add(property);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MusicDiscShelfBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(
                Properties.HORIZONTAL_FACING,
                ctx.getHorizontalPlayerFacing().getOpposite()
        );
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
        int slot = MusicDiscShelfLayout.slotFromHit(pos, state, hit);
        if (slot < 0 || ModItems.findTrack(stack.getItem()).isEmpty()) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (world.isClient) {
            return ItemActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof MusicDiscShelfBlockEntity shelf)) {
            return ItemActionResult.FAIL;
        }
        if (!shelf.insert(slot, stack, player.isCreative())) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        updateSlotOccupied(world, pos, state, slot, true);
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected ActionResult onUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            BlockHitResult hit
    ) {
        if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
            return ActionResult.PASS;
        }

        int slot = MusicDiscShelfLayout.slotFromHit(pos, state, hit);
        if (slot < 0) {
            return ActionResult.PASS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof MusicDiscShelfBlockEntity shelf)) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ItemStack removed = shelf.take(slot);
        if (removed.isEmpty()) {
            return ActionResult.PASS;
        }

        updateSlotOccupied(world, pos, state, slot, false);
        if (!player.getInventory().insertStack(removed)) {
            player.dropItem(removed, false);
        }
        return ActionResult.SUCCESS;
    }

    private static void updateSlotOccupied(
            World world,
            BlockPos pos,
            BlockState state,
            int slot,
            boolean occupied
    ) {
        BooleanProperty property = SLOT_OCCUPIED_PROPERTIES.get(slot);
        BlockState updatedState = state.with(property, occupied);
        if (updatedState != state) {
            world.setBlockState(pos, updatedState, Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MusicDiscShelfBlockEntity shelf) {
                ItemScatterer.spawn(world, pos, shelf);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}