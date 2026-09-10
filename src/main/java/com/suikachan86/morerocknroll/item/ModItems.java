package com.suikachan86.morerocknroll.item;

import com.suikachan86.morerocknroll.track.ModTracks;
import com.suikachan86.morerocknroll.track.TrackDefinition;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    private static final Map<String, Item> ITEMS = registerAll();

    // 所有唱片物品都由统一曲目清单注册。
    private static Map<String, Item> registerAll() {
        Map<String, Item> items = new LinkedHashMap<>();
        for (TrackDefinition track : ModTracks.ALL) {
            Item item = Registry.register(
                    Registries.ITEM,
                    track.itemId(),
                    new Item(new Item.Settings()
                            .maxCount(1)
                            .rarity(track.rarity())
                            .jukeboxPlayable(track.jukeboxSongKey())
                    )
            );
            items.put(track.id(), item);
        }
        return Map.copyOf(items);
    }

    public static Item get(TrackDefinition track) {
        Item item = ITEMS.get(track.id());
        if (item == null) {
            throw new IllegalArgumentException("Unknown track: " + track.id());
        }
        return item;
    }

    public static void initialize() {
        // 空方法，仅用于触发类的静态加载
    }
}
