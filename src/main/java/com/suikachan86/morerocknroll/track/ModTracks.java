package com.suikachan86.morerocknroll.track;

import net.minecraft.util.Rarity;

import java.util.List;
import java.util.Map;

/**
 * The single source of truth for the mod's music-disc catalogue.
 */
public final class ModTracks {
    public static final List<TrackDefinition> ALL = List.of(
            track("nanmonee", 171.0f, 15, Rarity.EPIC,
                    "Wasureranneyo's music disk", "Nanmonee",
                    "忘れらんねえよ的唱片", "なんもねえ"),
            track("kakumei", 98.0f, 15, Rarity.EPIC,
                    "andymori's music disk", "Kakumei",
                    "andymori的唱片", "革命"),
            track("time", 413.0f, 14, Rarity.EPIC,
                    "Pink Floyd's music disk", "Time",
                    "Pink Floyd的唱片", "Time"),
            track("homelandii", 213.0f, 15, Rarity.EPIC,
                    "Tayu Lo's music disk", "Homeland II",
                    "罗大佑的唱片", "原乡 II"),
            track("in_the_aeroplane_over_the_sea", 202.0f, 14, Rarity.EPIC,
                    "Neutral Milk Hotel's music disk", "In the Aeroplane Over the Sea",
                    "Neutral Milk Hotel的唱片", "In the Aeroplane Over the Sea"),
            track("summer68", 329.0f, 14, Rarity.EPIC,
                    "Pink Floyd's music disk", "Summer '68",
                    "Pink Floyd的唱片", "Summer '68"),
            track("siberian_khatru", 535.0f, 14, Rarity.EPIC,
                    "Yes's music disk'", "Siberian Khatru",
                    "Yes的唱片", "Siberian Khatru"),
            track("dancing_with_my_own_shadow", 360.0f, 15, Rarity.EPIC,
                    "Danne Band's music disk", "Dancing with My Own Shadow",
                    "丹娜乐团的唱片", "Dancing with My Own Shadow"),
            track("6_gram_star", 617.0f, 15, Rarity.EPIC,
                    "Shizuka's music disk", "6 Gram Star",
                    "静香的唱片", "6グラムの星")
    );

    private static TrackDefinition track(
            String id,
            float lengthSeconds,
            int comparatorOutput,
            Rarity rarity,
            String enUsItemName,
            String enUsSongName,
            String zhCnItemName,
            String zhCnSongName
    ) {
        return new TrackDefinition(
                id,
                lengthSeconds,
                comparatorOutput,
                rarity,
                Map.of(
                        "en_us", new TrackDefinition.Translation(enUsItemName, enUsSongName),
                        "zh_cn", new TrackDefinition.Translation(zhCnItemName, zhCnSongName)
                )
        );
    }

    private ModTracks() {
    }
}
