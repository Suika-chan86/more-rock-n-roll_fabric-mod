package com.suikachan86.morerocknroll.sound;

import com.suikachan86.morerocknroll.MoreRockNRoll;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {

    // 普通声音（用于方块等）
//    @SuppressWarnings("unused") // 压制“未使用”警告
//    public static final SoundEvent PROSPECTOR_FOUND_ORE = register("prospector_found_ore");

    // 唱片专用声音（返回 Reference，方便 JukeboxSong 使用）
    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_NANMONEE = registerReference("music_disc.nanmonee");
    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_KAKUMEI = registerReference("music_disc.kakumei");
    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_TIME = registerReference("music_disc.time");
    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_HOMELANDII = registerReference("music_disc.homelandii");
    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_IN_THE_AEROPLANE_OVER_THE_SEA = registerReference("music_disc.in_the_aeroplane_over_the_sea");
    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_SUMMER68 = registerReference("music_disc.summer68");
    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_SIBERIAN_KHATRU = registerReference("music_disc.siberian_khatru");
    public static final RegistryEntry.Reference<SoundEvent> MUSIC_DISC_DANCING_WITH_MY_OWN_SHADOW = registerReference("music_disc.dancing_with_my_own_shadow");

    // 注册普通声音（返回 SoundEvent）
//    @SuppressWarnings({"unused", "SameParameterValue"}) // 压制“未使用”警告, 压制“参数值固定”警告
//    private static SoundEvent register(String name) {
//        Identifier id = Identifier.of(MoreRockNRoll.MOD_ID, name);
//        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
//    }

    // 注册唱片专用声音（返回 RegistryEntry.Reference<SoundEvent>）
    private static RegistryEntry.Reference<SoundEvent> registerReference(String name) {
        Identifier id = Identifier.of(MoreRockNRoll.MOD_ID, name);
        return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void initialize() {
        // 空方法，触发类加载
    }
}

