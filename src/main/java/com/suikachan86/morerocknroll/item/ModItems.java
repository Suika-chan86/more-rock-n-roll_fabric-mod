package com.suikachan86.morerocknroll.item;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import com.suikachan86.morerocknroll.sound.ModJukeboxSongs;
import com.suikachan86.morerocknroll.sound.ModSoundEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;


public class ModItems {

    // なんもねえ（忘れらんねえよ）-- 尼古喵喵
    public static final Item MUSIC_DISC_NANMONEE = registerItems(
            "music_disc_nanmonee",  // 物品ID
            new Item(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .jukeboxPlayable(ModJukeboxSongs.key("nanmonee"))
            )
    );

    // 革命（andymori）
    public static final Item MUSIC_DISC_KAKUMEI = registerItems(
            "music_disc_kakumei",  // 物品ID
            new Item(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .jukeboxPlayable(ModJukeboxSongs.key("kakumei"))
            )
    );

    // Time（Pink Floyd）
    public static final Item MUSIC_DISC_TIME = registerItems(
            "music_disc_time",  // 物品ID
            new Item(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .jukeboxPlayable(ModJukeboxSongs.key("time"))
            )
    );

    // Summer '68（Pink Floyd）
    public static final Item MUSIC_DISC_SUMMER68 = registerItems(
            "music_disc_summer68",  // 物品ID
            new Item(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .jukeboxPlayable(ModJukeboxSongs.key("summer68"))
            )
    );

    // 原乡II（罗大佑）
    public static final Item MUSIC_DISC_HOMELANDII = registerItems(
            "music_disc_homelandii",  // 物品ID
            new Item(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .jukeboxPlayable(ModJukeboxSongs.key("homelandii"))
            )
    );

    // In the Aeroplane Over the Sea（Neutral Milk Hotel）
    public static final Item MUSIC_DISC_IN_THE_AEROPLANE_OVER_THE_SEA = registerItems(
            "music_disc_in_the_aeroplane_over_the_sea",  // 物品ID
            new Item(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .jukeboxPlayable(ModJukeboxSongs.key("in_the_aeroplane_over_the_sea"))
            )
    );

    // Siberian Khatru（Yes）
    public static final Item MUSIC_DISC_SIBERIAN_KHATRU = registerItems(
            "music_disc_siberian_khatru",  // 物品ID
            new Item(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .jukeboxPlayable(ModJukeboxSongs.key("siberian_khatru"))
            )
    );

    // Dancing With My Own Shadow（丹娜乐团）
    public static final Item MUSIC_DISC_DANCING_WITH_MY_OWN_SHADOW = registerItems(
            "music_disc_dancing_with_my_own_shadow",  // 物品ID
            new Item(new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.EPIC)
                    .jukeboxPlayable(ModJukeboxSongs.key("dancing_with_my_own_shadow"))
            )
    );


    private static Item registerItems(String id, Item item) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(MoreRockNRoll.MOD_ID, id),
                item
        );
    }

    public static void initialize() {
        // 空方法，仅用于触发类的静态加载
    }
}
