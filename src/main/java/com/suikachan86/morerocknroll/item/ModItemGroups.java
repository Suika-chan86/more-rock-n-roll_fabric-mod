package com.suikachan86.morerocknroll.item;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import com.suikachan86.morerocknroll.track.ModTracks;
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
					.icon(() -> new ItemStack(ModItems.get(ModTracks.ALL.getFirst())))
					.entries((displayContext, entries) -> {
						for (var track : ModTracks.ALL) {
							entries.add(ModItems.get(track));
						}
					})
					.build()
	);

	public static void initialize() {
		// 空方法，触发类加载
	}
}
