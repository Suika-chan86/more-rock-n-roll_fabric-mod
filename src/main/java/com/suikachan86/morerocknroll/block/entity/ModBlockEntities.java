package com.suikachan86.morerocknroll.block.entity;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import com.suikachan86.morerocknroll.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
    public static final BlockEntityType<MusicPlayerBlockEntity> MUSIC_PLAYER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            MoreRockNRoll.id("music_player"),
            BlockEntityType.Builder.create(MusicPlayerBlockEntity::new, ModBlocks.MUSIC_PLAYER).build()
    );

    private ModBlockEntities() {
    }

    public static void initialize() {
        // Loads the class so the static registration runs during mod initialization.
    }
}