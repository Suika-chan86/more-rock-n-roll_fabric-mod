package com.suikachan86.morerocknroll.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class MusicDiscShelfLayout {
    public static final int SLOT_COUNT = 6;
    private static final double DISPLAY_LEFT = 2.0 / 16.0;
    private static final double DISPLAY_WIDTH = 12.0 / 16.0;

    private MusicDiscShelfLayout() {
    }

    /**
     * Maps the hit to the nearest visual slot center.
     * The outer slots intentionally absorb the side frame as a click buffer,
     * which keeps the hit zones aligned with the six display plates.
     */
    public static int slotFromHit(BlockPos pos, BlockState state, BlockHitResult hit) {
        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        Vec3d local = hit.getPos().subtract(Vec3d.of(pos));
        double horizontal = switch (facing) {
            case NORTH, SOUTH -> local.x;
            case EAST, WEST -> local.z;
            default -> -1.0;
        };

        if (facing == Direction.NORTH || facing == Direction.WEST) {
            horizontal = 1.0 - horizontal;
        }

        if (horizontal < 0.0 || horizontal > 1.0) {
            return -1;
        }

        double slotWidth = DISPLAY_WIDTH / SLOT_COUNT;
        double firstCenter = DISPLAY_LEFT + slotWidth / 2.0;
        int nearestSlot = (int) Math.floor((horizontal - firstCenter) / slotWidth + 0.5);
        return Math.max(0, Math.min(SLOT_COUNT - 1, nearestSlot));
    }
}