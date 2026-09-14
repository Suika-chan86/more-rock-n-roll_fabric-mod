package com.suikachan86.morerocknroll;

import com.suikachan86.morerocknroll.block.ModBlocks;
import com.suikachan86.morerocknroll.item.ModItems;
import com.suikachan86.morerocknroll.track.ModTracks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.util.Identifier;

/**
 * datagen 生成物品模型。
 * <p>
 * 每个物品一行 register，会自动生成 models/item/<物品id>.json，
 * layer0 自动指向 assets/<命名空间>/textures/item/<物品id>.png。
 */
public class ModModelProvider extends FabricModelProvider {

	public ModModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
		// Use the vanilla jukebox texture as a temporary visual placeholder.
		blockStateModelGenerator.registerSingleton(
				ModBlocks.MUSIC_PLAYER,
				block -> TexturedModel.getCubeAll(Identifier.of("minecraft", "block/jukebox_side"))
		);
		blockStateModelGenerator.registerParentedItemModel(
				ModBlocks.MUSIC_PLAYER,
				MoreRockNRoll.id("block/music_player")
		);
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {
		// 唱片：扁平物品（item/generated），贴图按统一曲目 ID 自动推导。
		for (var track : ModTracks.ALL) {
			itemModelGenerator.register(ModItems.get(track), Models.GENERATED);
		}
	}
}
