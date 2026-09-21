package com.suikachan86.morerocknroll;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.suikachan86.morerocknroll.block.MusicDiscShelfBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generates the state-driven visual layer of the music-disc shelf.
 *
 * <p>The six models describe fixed display positions. They intentionally do
 * not use the identity of the item stored in the block entity; the block
 * entity is the storage boundary and the block state is the visual boundary.</p>
 */
public final class MusicDiscShelfModelProvider implements DataProvider {
    private static final List<Direction> HORIZONTAL_DIRECTIONS = List.of(
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    );

    // Temporary placeholders. Replace these six texture IDs with the final shelf art later.
    private static final List<Identifier> SLOT_TEXTURES = List.of(
            MoreRockNRoll.id("item/music_disc_nanmonee"),
            MoreRockNRoll.id("item/music_disc_kakumei"),
            MoreRockNRoll.id("item/music_disc_time"),
            MoreRockNRoll.id("item/music_disc_homelandii"),
            MoreRockNRoll.id("item/music_disc_in_the_aeroplane_over_the_sea"),
            MoreRockNRoll.id("item/music_disc_summer68")
    );

    private final Path assetRoot;

    public MusicDiscShelfModelProvider(FabricDataOutput output) {
        this.assetRoot = output.getPath()
                .resolve("assets")
                .resolve(MoreRockNRoll.MOD_ID);
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        writes.add(DataProvider.writeToPath(
                writer,
                createBlockStateJson(),
                assetRoot.resolve("blockstates/music_disc_shelf.json")
        ));

        for (int slot = 0; slot < MusicDiscShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); slot++) {
            writes.add(DataProvider.writeToPath(
                    writer,
                    createSlotModelJson(slot),
                    assetRoot.resolve("models/block/music_disc_shelf_slot_" + slot + ".json")
            ));
        }

        return CompletableFuture.allOf(writes.toArray(CompletableFuture<?>[]::new));
    }

    @Override
    public String getName() {
        return "Music Disc Shelf Models";
    }

    private JsonObject createBlockStateJson() {
        JsonObject root = new JsonObject();
        JsonArray multipart = new JsonArray();

        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            int rotation = yRotation(direction);
            addMultipart(multipart, condition(direction, null), "block/music_disc_shelf_base", rotation);

            for (int slot = 0; slot < MusicDiscShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); slot++) {
                BooleanProperty property = MusicDiscShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot);
                addMultipart(
                        multipart,
                        condition(direction, property.getName()),
                        "block/music_disc_shelf_slot_" + slot,
                        rotation
                );
            }
        }

        root.add("multipart", multipart);
        return root;
    }

    private JsonObject createSlotModelJson(int slot) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");

        JsonObject textures = new JsonObject();
        String texture = SLOT_TEXTURES.get(slot).toString();
        textures.addProperty("disc", texture);
        textures.addProperty("particle", texture);
        root.add("textures", textures);

        double interiorStart = 2.0;
        double interiorWidth = 12.0;
        double slotStart = interiorStart
                + slot * interiorWidth / MusicDiscShelfBlock.SLOT_OCCUPIED_PROPERTIES.size() + 0.15;
        double slotEnd = interiorStart
                + (slot + 1) * interiorWidth / MusicDiscShelfBlock.SLOT_OCCUPIED_PROPERTIES.size() - 0.15;

        JsonObject element = new JsonObject();
        element.add("from", coordinates(slotStart, 3.0, 0.0));
        element.add("to", coordinates(slotEnd, 5.5, 1.0));

        JsonObject faces = new JsonObject();
        faces.add("north", face("#disc"));
        faces.add("south", face("#disc"));
        faces.add("east", face("#disc"));
        faces.add("west", face("#disc"));
        faces.add("up", face("#disc"));
        faces.add("down", face("#disc"));
        element.add("faces", faces);

        JsonArray elements = new JsonArray();
        elements.add(element);
        root.add("elements", elements);
        return root;
    }

    private static JsonObject condition(Direction direction, String occupiedProperty) {
        JsonObject condition = new JsonObject();
        condition.addProperty("facing", direction.getName());
        if (occupiedProperty != null) {
            condition.addProperty(occupiedProperty, true);
        }
        return condition;
    }

    private static void addMultipart(JsonArray multipart, JsonObject condition, String model, int rotation) {
        JsonObject part = new JsonObject();
        part.add("when", condition);

        JsonObject apply = new JsonObject();
        apply.addProperty("model", MoreRockNRoll.id(model).toString());
        if (rotation != 0) {
            apply.addProperty("y", rotation);
        }
        part.add("apply", apply);
        multipart.add(part);
    }

    private static JsonArray coordinates(double x, double y, double z) {
        JsonArray coordinates = new JsonArray();
        coordinates.add(x);
        coordinates.add(y);
        coordinates.add(z);
        return coordinates;
    }

    private static JsonObject face(String texture) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", texture);
        return face;
    }

    private static int yRotation(Direction direction) {
        return switch (direction) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> throw new IllegalArgumentException("Unsupported shelf direction: " + direction);
        };
    }
}