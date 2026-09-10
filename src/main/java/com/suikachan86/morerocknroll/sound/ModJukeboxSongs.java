package com.suikachan86.morerocknroll.sound;

import com.suikachan86.morerocknroll.track.ModTracks;
import com.suikachan86.morerocknroll.track.TrackDefinition;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registerable;
import net.minecraft.text.Text;

/**
 * 唱片的 JukeboxSong 注册表键。
 * <p>
 * JukeboxSong 属于数据包动态注册表（RegistryKeys.JUKEBOX_SONG），
 * 它的实际内容由数据包文件定义。
 */
public final class ModJukeboxSongs {
    private ModJukeboxSongs() {
    }

	// datagen 调用它，把清单里的歌「登记」进临时注册表
	public static void bootstrap(Registerable<JukeboxSong> registry) {
		for (TrackDefinition track : ModTracks.ALL) {
			registry.register(
					track.jukeboxSongKey(),
					new JukeboxSong(
							ModSoundEvents.get(track),
							Text.translatable(track.jukeboxSongTranslationKey()),
							track.lengthSeconds(),
							track.comparatorOutput()
					)
			);
		}
	}
}
