package com.suikachan86.morerocknroll.block;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final MusicDiscShelfBlock MUSIC_DISC_SHELF = register(
            "music_disc_shelf",
            new MusicDiscShelfBlock(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.OAK_TAN)
                            .strength(2.0f)
                            .sounds(BlockSoundGroup.WOOD)
            )
    );

    public static final MusicPlayerBlock MUSIC_PLAYER = register(
            "music_player",
            new MusicPlayerBlock(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.IRON_GRAY)
                            .strength(2.0f)
                            .sounds(BlockSoundGroup.METAL)
            )
    );

    private ModBlocks() {
    }

    private static <T extends Block> T register(String name, T block) {
        Identifier id = MoreRockNRoll.id(name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {
        // Loads the class so the static registrations run during mod initialization.
    }
}
