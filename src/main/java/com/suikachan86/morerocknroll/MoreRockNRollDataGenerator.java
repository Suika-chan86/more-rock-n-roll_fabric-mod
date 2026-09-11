package com.suikachan86.morerocknroll;

import com.suikachan86.morerocknroll.sound.ModJukeboxSongs;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public class MoreRockNRollDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		// 创建一个数据包：datagen 生成的 JSON 会写入 src/main/generated
		final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		// ★ 关键一步：把登记好的歌曲「倒」成 JSON 文件
		pack.addProvider((output, registriesFuture) -> new FabricDynamicRegistryProvider(output, registriesFuture) {

			@Override
			protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
				// addAll：把这个注册表里「所有属于本 mod 命名空间」的条目都写出去
				entries.addAll(registries.getWrapperOrThrow(RegistryKeys.JUKEBOX_SONG));
			}

			@Override
			public String getName() {
				return "Jukebox Songs";
			}
		});

		// 生成物品模型（models/item/...）
		pack.addProvider(ModModelProvider::new);

		// 生成语言文件（assets/<mod id>/lang/...）
		pack.addProvider((output, registriesFuture) ->
				new ModLanguageProvider(output, "en_us", registriesFuture));
		pack.addProvider((output, registriesFuture) ->
				new ModLanguageProvider(output, "zh_cn", registriesFuture));

		// 根据 ModTracks.ALL 完整生成 assets/<mod id>/sounds.json
		pack.addProvider(ModSoundProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		// 声明：JUKEBOX_SONG 的条目由 ModJukeboxSongs::bootstrap 提供
		registryBuilder.addRegistry(RegistryKeys.JUKEBOX_SONG, ModJukeboxSongs::bootstrap);
	}
}
