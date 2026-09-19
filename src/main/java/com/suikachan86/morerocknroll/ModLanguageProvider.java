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
    private static final Map<String, String> BLOCK_NAMES = Map.of(
            "en_us", "Music Player",
            "zh_cn", "唱片播放器"
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

        translationBuilder.add(
                "block.more-rock-n-roll.music_player",
                BLOCK_NAMES.get(languageCode)
        );

        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.select_track",
                languageCode.equals("zh_cn") ? "选择曲目" : "Select track"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.pause",
                languageCode.equals("zh_cn") ? "暂停" : "Pause"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.resume",
                languageCode.equals("zh_cn") ? "恢复" : "Resume"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.stop",
                languageCode.equals("zh_cn") ? "停止" : "Stop"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.keyboard_hint",
                languageCode.equals("zh_cn") ? "方向键选择 | 回车确认" : "Arrow keys: Select | Enter: Confirm"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.current_track",
                languageCode.equals("zh_cn") ? "当前曲目：%s" : "Current track: %s"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.no_track",
                languageCode.equals("zh_cn") ? "尚未选择曲目" : "No track selected"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.status",
                languageCode.equals("zh_cn") ? "状态：%s" : "Status: %s"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.status.playing",
                languageCode.equals("zh_cn") ? "播放中" : "Playing"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.status.paused",
                languageCode.equals("zh_cn") ? "已暂停" : "Paused"
        );
        translationBuilder.add(
                "screen.more-rock-n-roll.music_player.status.stopped",
                languageCode.equals("zh_cn") ? "已停止" : "Stopped"
        );

        for (var track : ModTracks.ALL) {
            var translation = track.translationFor(languageCode);
            translationBuilder.add(track.itemTranslationKey(), translation.itemName());
            translationBuilder.add(track.jukeboxSongTranslationKey(), translation.songName());
        }
    }
}
