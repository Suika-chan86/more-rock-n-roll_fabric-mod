package com.suikachan86.morerocknroll;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.suikachan86.morerocknroll.track.ModTracks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class ModSoundProvider implements DataProvider {
    private final FabricDataOutput dataOutput;

    public ModSoundProvider(FabricDataOutput dataOutput) {
        this.dataOutput = dataOutput;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        JsonObject soundsJson = new JsonObject();

        for (var track : ModTracks.ALL) {
            JsonObject soundEntry = new JsonObject();
            JsonArray sounds = new JsonArray();
            JsonObject sound = new JsonObject();
            sound.addProperty("name", track.soundId().toString());
            sound.addProperty("stream", true);
            sounds.add(sound);
            soundEntry.add("sounds", sounds);
            soundsJson.add("music_disc." + track.id(), soundEntry);
        }

        Path outputPath = dataOutput
                .getResolver(DataOutput.OutputType.RESOURCE_PACK, "")
                .resolveJson(MoreRockNRoll.id("sounds"));
        return DataProvider.writeToPath(writer, soundsJson, outputPath);
    }

    @Override
    public String getName() {
        return "Sounds";
    }
}
