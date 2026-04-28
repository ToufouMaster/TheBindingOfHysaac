package fr.toufoumaster.utils;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import fr.toufoumaster.dungeons.Rng;
import fr.toufoumaster.dungeons.Room;
import fr.toufoumaster.dungeons.RoomShape;
import fr.toufoumaster.dungeons.RoomType;

import javax.annotation.Nonnull;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Vector;

public class RoomRegister {
    static HashMap<String, BlockSelection> allRooms;
    static HashMap<RoomType, Vector<String>> roomsByType;
    static HashMap<RoomShape, Vector<String>> roomsByShape;
    static HashMap<String, Short> roomsByDoors;

    public static void register() {
        // Read all the files and store each prefabs
        allRooms = new HashMap<>();
        roomsByType = new HashMap<>();
        roomsByShape = new HashMap<>();
        roomsByDoors = new HashMap<>();

        final Path prefabStorePath = PrefabStore.get().getServerPrefabsPath();
        if (Files.isDirectory(prefabStorePath)) {
            try {
                Files.walkFileTree(prefabStorePath, FileUtil.DEFAULT_WALK_TREE_OPTIONS_SET, Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                    @Nonnull
                    public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                        String fileName = file.getFileName().toString();
                        String parentPath = file.getParent().toString();

                        if (fileName.endsWith(".prefab.json") && !parentPath.startsWith("prefabs\\templates")) {
                            String filePath = "prefabs/"+PathUtil.relativize(prefabStorePath, file);
                            RoomType type = RoomType.ROOM_DEFAULT;
                            if (parentPath.startsWith("prefabs\\treasure")) {
                                type = RoomType.ROOM_TREASURE;
                            }

                            RoomShape shape = RoomShape.ROOMSHAPE_NULL;
                            for (RoomShape s : RoomShape.values()) {
                                if (!fileName.endsWith("_"+s.name()+".prefab.json")) continue;
                                shape = s;
                            }

                            BlockSelection prefab = PrefabStore.get().getPrefab(file);

                            Vector<String> roomTypeNames = roomsByType.getOrDefault(type, new Vector<>());
                            roomTypeNames.add(filePath);
                            roomsByType.put(type, roomTypeNames);

                            Vector<String> roomShapeNames = roomsByShape.getOrDefault(shape, new Vector<>());
                            roomShapeNames.add(filePath);
                            roomsByShape.put(shape, roomShapeNames);

                            short doorMask = getPrefabDoorMask(shape, prefab);
                            roomsByDoors.put(filePath, doorMask);

                            allRooms.put(filePath, prefab);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (Exception _) {

            }
        }
    }

    public static short getPrefabDoorMask(RoomShape shape, BlockSelection prefab) {
        Vector2i[] shapeDoors = shapeDoorLocations.get(shape);
        BlockTypeAssetMap<String, BlockType> blockTypeAssetMap = BlockType.getAssetMap();
        short mask = 0;
        for (short i = 0; i < shapeDoors.length; i++) {
            if (shapeDoors[i] == null) continue;
            Vector2i shapeDoor = shapeDoors[i].clone().add(0, 16);
            int blockId = prefab.getBlockAtWorldPos(shapeDoor.getX(), 2, shapeDoor.getY());
            BlockType blockType = blockTypeAssetMap.getAsset(blockId);
            if (blockType == null) continue;
            mask += blockType.getId().equals("DoorClosed_Basement") ? (short) (1 << i) : 0;
        }
        return mask;
    }

    public static BlockSelection getRandomRoomFrom(Rng rng, RoomType type, RoomShape shape, short doorMask) {
        Vector<BlockSelection> selectedRooms = getRoomsFrom(type, shape, doorMask);
        if (selectedRooms.isEmpty()) return null;
        return selectedRooms.get(rng.nextInt(selectedRooms.size()));
    }

    public static Vector<String> getRoomNamesFrom(RoomType type, RoomShape shape, short doorMask) {
        Vector<String> roomNames = new Vector<>(allRooms.keySet());
        roomNames.retainAll(roomsByType.getOrDefault(type, new Vector<>()));
        roomNames.retainAll(roomsByShape.getOrDefault(shape, new Vector<>()));
        Vector<String> roomsByDoorMask = new Vector<>();
        roomsByDoors.forEach((name, curDoorMask) -> {
            if ((curDoorMask | doorMask) == curDoorMask) roomsByDoorMask.add(name);
        });
        roomNames.retainAll(roomsByDoorMask);
        return roomNames;
    }

    public static Vector<BlockSelection> getRoomsFrom(RoomType type, RoomShape shape, short doorMask) {
        Vector<BlockSelection> rooms = new Vector<>();
        Vector<String> roomNames = getRoomNamesFrom(type, shape, doorMask);
        allRooms.forEach((roomName, room) -> {
            if (roomNames.contains(roomName)) rooms.add(room);
        });
        return rooms;
    }

    public static HashMap<RoomShape, Vector2i[]> shapeDoorLocations = new HashMap<>() {
        {
            put(RoomShape.ROOMSHAPE_1x1, new Vector2i[] { new Vector2i(-6, 0), new Vector2i(0, -3), new Vector2i(6, 0), new Vector2i(0, 3) });
            put(RoomShape.ROOMSHAPE_IH, new Vector2i[] { new Vector2i(-6, 0), null, new Vector2i(6, 0), null });
            put(RoomShape.ROOMSHAPE_IV, new Vector2i[] { null, new Vector2i(0, -3), null, new Vector2i(0, 3) });

            //                                            L1                         U1                            R1                                    D1                             L2                              U2                             R2                             D2
            put(RoomShape.ROOMSHAPE_1x2, new Vector2i[] { new Vector2i(-6, -3),  new Vector2i(0, -6),   new Vector2i(6, -3),             new Vector2i(0, 7),      new Vector2i(-6, 4),      null,                          new Vector2i(6, 4) });
            put(RoomShape.ROOMSHAPE_IIV, new Vector2i[] { null,                       new Vector2i(0, -6),   null,                                 new Vector2i(0, 7) });
            put(RoomShape.ROOMSHAPE_2x1, new Vector2i[] { new Vector2i(-13, 0), new Vector2i(-7, -3),   new Vector2i(12, 0),            new Vector2i(-7, 3),      null,                            new Vector2i(6, -3),    null,                          new Vector2i(-7, 3) });
            put(RoomShape.ROOMSHAPE_IIH, new Vector2i[] { new Vector2i(-13, 0), null,                        new Vector2i(12, 0),            null });

            put(RoomShape.ROOMSHAPE_2x2, new Vector2i[] { new Vector2i(-13, -4), new Vector2i(-7, -7),   new Vector2i(12, -4),            new Vector2i(-7, 6),      new Vector2i(-13, 3),      new Vector2i(6, -7),     new Vector2i(12, 3),   new Vector2i(6, 6) });
            put(RoomShape.ROOMSHAPE_LTL, new Vector2i[] { new Vector2i(0, -4), new Vector2i(-7, 0),   new Vector2i(12, -4),            new Vector2i(-7, 6),      new Vector2i(-13, 3),      new Vector2i(6, -7),     new Vector2i(12, 3),   new Vector2i(6, 6) });
            put(RoomShape.ROOMSHAPE_LTR, new Vector2i[] { new Vector2i(-13, -4), new Vector2i(-7, -7),   new Vector2i(-1, -4),            new Vector2i(-7, 6),      new Vector2i(-13, 3),      new Vector2i(6, 0),     new Vector2i(12, 3),   new Vector2i(6, 6) });
            put(RoomShape.ROOMSHAPE_LBL, new Vector2i[] { new Vector2i(-13, -4), new Vector2i(-7, -7),   new Vector2i(12, -4),            new Vector2i(-7, -1),      new Vector2i(0, 3),      new Vector2i(6, -7),     new Vector2i(12, 3),   new Vector2i(6, 6) });
            put(RoomShape.ROOMSHAPE_LBR, new Vector2i[] { new Vector2i(-13, -4), new Vector2i(-7, -7),   new Vector2i(12, -4),            new Vector2i(-7, 6),      new Vector2i(-13, 3),      new Vector2i(6, -7),     new Vector2i(-1, 3),   new Vector2i(6, -1) });
        }
    };

    public static int doorsToBits(RoomShape shape, HashMap<Vector2i, Boolean> doors) {
        Vector2i[] locs = shapeDoorLocations.get(shape);
        var bits = 0;
        for (var i = 0; i < locs.length; i++) {
            if (locs[i] == null)
                continue;

            if (doors.containsKey(locs[i]))
                bits |= (1 << i);
        }
        return bits;
    }

    public static int getDoorIdFromCoords(RoomShape shape, Vector2i coords) {
        Vector2i[] doorPositions = RoomRegister.shapeDoorLocations.get(shape);
        for (int i = 0; i < doorPositions.length; i++) {
            Vector2i doorPos = doorPositions[i];
            if (doorPos == null || !doorPos.equals(coords)) continue;
            return i;
        }
        return -1;
    }

    public static short getRoomDoorMask(Room r) {
        short mask = 0;
        HashMap<Integer, Room> neighbours = r.getNeighbors();
        if (neighbours == null) return mask;
        for (int i : neighbours.keySet()) {
            mask += neighbours.get(i) != null ? (short) (1 << i) : 0;
        }
        return mask;
    }
}
