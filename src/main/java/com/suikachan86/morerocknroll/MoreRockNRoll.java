package com.suikachan86.morerocknroll;

import com.suikachan86.morerocknroll.block.ModBlocks;
import com.suikachan86.morerocknroll.block.entity.ModBlockEntities;
import com.suikachan86.morerocknroll.item.ModItemGroups;
import com.suikachan86.morerocknroll.item.ModItems;
import com.suikachan86.morerocknroll.network.MusicPlayerNetworking;
import com.suikachan86.morerocknroll.screen.ModScreenHandlers;
import com.suikachan86.morerocknroll.sound.ModSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreRockNRoll implements ModInitializer {
	public static final String MOD_ID = "more-rock-n-roll";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// Register the server-to-client playback state channel before any action is sent.
		MusicPlayerNetworking.initialize();

		// 加载模组声音
		ModSoundEvents.initialize();
		LOGGER.info("Loading Mod sounds…");

		// 加载物品
          ModItems.initialize();
          LOGGER.info("Loading Mod items…");

          // 加载方块和方块物品
          ModBlocks.initialize();
          LOGGER.info("Loading Mod blocks…");

          // 加载方块实体
          ModBlockEntities.initialize();
          LOGGER.info("Loading Mod block entities…");

          // 加载界面
          ModScreenHandlers.initialize();
          LOGGER.info("Loading Mod screen handlers…");

          // 加载物品组（创造模式标签）
		ModItemGroups.initialize();
		LOGGER.info("Loading Mod item groups…");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}