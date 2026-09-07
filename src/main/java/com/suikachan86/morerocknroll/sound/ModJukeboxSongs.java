package com.suikachan86.morerocknroll.sound;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import java.util.List;

/**
 * 唱片的 JukeboxSong 注册表键。
 * <p>
 * JukeboxSong 属于数据包动态注册表（RegistryKeys.JUKEBOX_SONG），
 * 它的实际内容由数据包文件定义。
 */
public interface ModJukeboxSongs {

	// 一首歌的「配方」：路径名 + 声音事件 + 时长(秒) + 红石比较器信号
	record Song(String id, RegistryEntry.Reference<SoundEvent> sound, float lengthSeconds, int comparatorOutput) {}

	List<Song> SONGS = List.of(
			new Song("nanmonee", ModSoundEvents.MUSIC_DISC_NANMONEE, 171.0f, 15),
			new Song("kakumei", ModSoundEvents.MUSIC_DISC_KAKUMEI, 98.0f, 15),
			new Song("time", ModSoundEvents.MUSIC_DISC_TIME, 413.0f, 14),
			new Song("homelandii", ModSoundEvents.MUSIC_DISC_HOMELANDII, 213.0f, 15),
			new Song("in_the_aeroplane_over_the_sea", ModSoundEvents.MUSIC_DISC_IN_THE_AEROPLANE_OVER_THE_SEA, 202.0f, 14),
			new Song("summer68", ModSoundEvents.MUSIC_DISC_SUMMER68, 329.0f, 14),
			new Song("siberian_khatru", ModSoundEvents.MUSIC_DISC_SIBERIAN_KHATRU, 535.0f, 14),
			new Song("dancing_with_my_own_shadow", ModSoundEvents.MUSIC_DISC_DANCING_WITH_MY_OWN_SHADOW, 360.0f, 15)


			// ……再加几十上百首
	);

	// 由路径名推导出歌曲条目的 RegistryKey（物品组件要引用它）
	static RegistryKey<JukeboxSong> key(String id) {
		return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, MoreRockNRoll.id(id));
	}

	// datagen 调用它，把清单里的歌「登记」进临时注册表
	static void bootstrap(Registerable<JukeboxSong> registry) {
		for (Song song : SONGS) {
			registry.register(
					key(song.id()),
					new JukeboxSong(
							song.sound(),
							Text.translatable(Util.createTranslationKey("jukebox_song", MoreRockNRoll.id(song.id()))),
							song.lengthSeconds(),
							song.comparatorOutput()
					)
			);
		}
	}
}
