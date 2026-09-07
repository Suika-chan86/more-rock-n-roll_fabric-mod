package com.suikachan86.morerocknroll;

import com.suikachan86.morerocknroll.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

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
		// 没有方块，留空
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {
		// 唱片：扁平物品（item/generated），贴图自动取 textures/item/music_disc_nanmonee.png
		itemModelGenerator.register(ModItems.MUSIC_DISC_NANMONEE, Models.GENERATED);
		itemModelGenerator.register(ModItems.MUSIC_DISC_KAKUMEI, Models.GENERATED);
		itemModelGenerator.register(ModItems.MUSIC_DISC_TIME, Models.GENERATED);
		itemModelGenerator.register(ModItems.MUSIC_DISC_HOMELANDII, Models.GENERATED);
		itemModelGenerator.register(ModItems.MUSIC_DISC_IN_THE_AEROPLANE_OVER_THE_SEA, Models.GENERATED);
		itemModelGenerator.register(ModItems.MUSIC_DISC_SUMMER68, Models.GENERATED);
		itemModelGenerator.register(ModItems.MUSIC_DISC_SIBERIAN_KHATRU, Models.GENERATED);
		itemModelGenerator.register(ModItems.MUSIC_DISC_DANCING_WITH_MY_OWN_SHADOW, Models.GENERATED);
	}
}
