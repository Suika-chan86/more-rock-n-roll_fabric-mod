package com.suikachan86.morerocknroll;

import com.suikachan86.morerocknroll.track.ModTracks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class ModLanguageProvider extends FabricLanguageProvider {
    private static final Map<String, String> ITEM_GROUP_NAMES = Map.of(
            "en_us", "More Rock n Roll",
            "zh_cn", "更多摇滚乐"
    );

    private final String languageCode;

    public ModLanguageProvider(
            FabricDataOutput dataOutput,
            String languageCode,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(dataOutput, languageCode, registryLookup);
        this.languageCode = languageCode;
    }

    @Override
    public void generateTranslations(
            RegistryWrapper.WrapperLookup registryLookup,
            TranslationBuilder translationBuilder
    ) {
        String itemGroupName = ITEM_GROUP_NAMES.get(languageCode);
        if (itemGroupName == null) {
            throw new IllegalArgumentException("Unsupported language: " + languageCode);
        }

        translationBuilder.add(
                "itemGroup.more-rock-n-roll.more_rock_n_roll",
                itemGroupName
        );

        for (var track : ModTracks.ALL) {
            var translation = track.translationFor(languageCode);
            translationBuilder.add(track.itemTranslationKey(), translation.itemName());
            translationBuilder.add(track.jukeboxSongTranslationKey(), translation.songName());
        }
    }
}
