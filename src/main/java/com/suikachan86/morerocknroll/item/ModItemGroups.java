package com.suikachan86.morerocknroll.item;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

	public static final ItemGroup MORE_ROCK_N_ROLL_GROUP = Registry.register(
			Registries.ITEM_GROUP,
			Identifier.of(MoreRockNRoll.MOD_ID, "more_rock_n_roll"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemGroup.more-rock-n-roll.more_rock_n_roll"))
					.icon(() -> new ItemStack(ModItems.MUSIC_DISC_NANMONEE))
					.entries((displayContext, entries) -> {
						entries.add(ModItems.MUSIC_DISC_NANMONEE);
						entries.add(ModItems.MUSIC_DISC_KAKUMEI);
						entries.add(ModItems.MUSIC_DISC_TIME);
						entries.add(ModItems.MUSIC_DISC_HOMELANDII);
						entries.add(ModItems.MUSIC_DISC_IN_THE_AEROPLANE_OVER_THE_SEA);
						entries.add(ModItems.MUSIC_DISC_SUMMER68);
						entries.add(ModItems.MUSIC_DISC_SIBERIAN_KHATRU);
						entries.add(ModItems.MUSIC_DISC_DANCING_WITH_MY_OWN_SHADOW);
					})
					.build()
	);

	public static void initialize() {
		// 空方法，触发类加载
	}
}
