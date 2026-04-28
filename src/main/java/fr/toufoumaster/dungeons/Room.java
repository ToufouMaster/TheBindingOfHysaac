package fr.toufoumaster.dungeons;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockFilter;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockMask;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.utils.EntitySpawnUtil;
import fr.toufoumaster.utils.ItemUtils;
import fr.toufoumaster.utils.PickupUtils;
import fr.toufoumaster.utils.RoomRegister;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import static fr.toufoumaster.commands.MonsterCommand.spawnMonster;

public class Room {
    private RoomShape shape;
    private Vector2i pos;
    private final Vector2i placedPos;
    private final int parentDirection;
    private HashMap<Integer, Room> neighbors; // Key is door id.
    private RoomType roomType;

    BlockSelection prefab;
    private boolean cleared;
    public int id;
    public int distance;
    public boolean isDeadEnd;
    private Vector<Ref<EntityStore>> aliveMonsters;

    public Room(RoomShape roomShape, Vector2i roomPos, Vector2i placedRoomPos, int parentDirection, int dist) {
        this.shape = roomShape;
        this.pos = roomPos;
        this.placedPos = placedRoomPos;
        this.parentDirection = parentDirection;
        this.distance = dist;
        this.roomType = RoomType.ROOM_DEFAULT;
        this.cleared = false;

        this.aliveMonsters = new Vector<>();
    }

    public Room(RoomShape roomShape, Vector2i roomPos, int parentDirection) {
        this(roomShape, roomPos, null, parentDirection, 0);
    }

    public RoomShape getShape() {
        return shape;
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }

    public boolean hasThreat(Store<EntityStore> store) {
        for (Ref<EntityStore> ref : aliveMonsters) {
            if (ref == null || !ref.isValid()) continue;
            IsaacMonsterComponent monsterComponent = store.getComponent(ref, IsaacMonsterComponent.getComponentType());
            if (monsterComponent == null) continue;
            if (monsterComponent.getAI().isThreat()) return true;
        }
        return false;
    }

    public void setNeighbours(HashMap<Integer, Room> neighbours) {
        this.neighbors = neighbours;
    }

    public HashMap<Integer, Room> getNeighbors() {
        return neighbors;
    }

    public int getParentDirection() {
        return parentDirection;
    }

    public static HashMap<RoomShape, Vector2i[]> shapeVectors = new HashMap<>()
    {{
        put(RoomShape.ROOMSHAPE_NULL, new Vector2i[] {});
        put(RoomShape.ROOMSHAPE_1x1, new Vector2i[] { new Vector2i(0, 0) });
        put(RoomShape.ROOMSHAPE_1x2, new Vector2i[] { new Vector2i(0, 0), new Vector2i(0, 1) });
        put(RoomShape.ROOMSHAPE_2x1, new Vector2i[] { new Vector2i(0, 0), new Vector2i(1, 0) });
        put(RoomShape.ROOMSHAPE_2x2, new Vector2i[] { new Vector2i(0, 0), new Vector2i(1, 0), new Vector2i(1, 1), new Vector2i(0, 1) });
        put(RoomShape.ROOMSHAPE_IH, new Vector2i[] { new Vector2i(0, 0) });
        put(RoomShape.ROOMSHAPE_IIH, new Vector2i[] { new Vector2i(0, 0), new Vector2i(1, 0) });
        put(RoomShape.ROOMSHAPE_IV, new Vector2i[] { new Vector2i(0, 0) });
        put(RoomShape.ROOMSHAPE_IIV, new Vector2i[] { new Vector2i(0, 0), new Vector2i(0, 1) });
        put(RoomShape.ROOMSHAPE_LTL, new Vector2i[] { new Vector2i(1, 0), new Vector2i(1, 1), new Vector2i(0, 1) });
        put(RoomShape.ROOMSHAPE_LTR, new Vector2i[] { new Vector2i(0, 0), new Vector2i(1, 1), new Vector2i(0, 1) });
        put(RoomShape.ROOMSHAPE_LBL, new Vector2i[] { new Vector2i(0, 0), new Vector2i(1, 0), new Vector2i(1, 1) });
        put(RoomShape.ROOMSHAPE_LBR, new Vector2i[] { new Vector2i(0, 0), new Vector2i(1, 0), new Vector2i(0, 1) });
    }};

    public static HashMap<RoomShape, Vector2i[]> shapeDoors = new HashMap<>()
    {
        // L U R D, L U R D
        {
            put(RoomShape.ROOMSHAPE_1x1, new Vector2i[] { new Vector2i(-1, 0), new Vector2i(0, -1),  new Vector2i(1, 0),  new Vector2i(0, 1),    null, null, null, null  });
            put(RoomShape.ROOMSHAPE_1x2, new Vector2i[] { new Vector2i(-1, 0), new Vector2i(0, -1),  new Vector2i(1, 0),  new Vector2i(0, 2),    new Vector2i(-1, 1), null, new Vector2i(1, 1), null  });
            put(RoomShape.ROOMSHAPE_2x1, new Vector2i[] { new Vector2i(-1, 0), new Vector2i(0, -1),  new Vector2i(2, 0),  new Vector2i(0, 1),    null, new Vector2i(1, -1), null, new Vector2i(1, 1) });
            put(RoomShape.ROOMSHAPE_2x2, new Vector2i[] { new Vector2i(-1, 0), new Vector2i(0, -1),  new Vector2i(2, 0),  new Vector2i(0, 2),    new Vector2i(-1, 1), new Vector2i(1, -1), new Vector2i(2, 1), new Vector2i(1, 2) });
            put(RoomShape.ROOMSHAPE_IH,  new Vector2i[] { new Vector2i(-1, 0), null, new Vector2i(1, 0), null,                                               null, null, null, null });
            put(RoomShape.ROOMSHAPE_IIH, new Vector2i[] { new Vector2i(-1, 0), null, new Vector2i(2, 0), null,                                               null, null, null, null });
            put(RoomShape.ROOMSHAPE_IV,  new Vector2i[] { null, new Vector2i(0, -1), null,  new Vector2i(0, 1),                                              null, null, null, null });
            put(RoomShape.ROOMSHAPE_IIV, new Vector2i[] { null, new Vector2i(0, -1), null,  new Vector2i(0, 2),                                              null, null, null, null });
            put(RoomShape.ROOMSHAPE_LTL, new Vector2i[] { new Vector2i(0, 0), new Vector2i(0, 0), new Vector2i(2, 0), new Vector2i(0, 2),        new Vector2i(-1, 1), new Vector2i(1, -1), new Vector2i(2, 1), new Vector2i(1, 2) } );
            put(RoomShape.ROOMSHAPE_LTR, new Vector2i[] { new Vector2i(-1, 0), new Vector2i(0, -1), new Vector2i(1, 0), new Vector2i(0, 2),      new Vector2i(-1, 1), new Vector2i(1, 0), new Vector2i(2, 1), new Vector2i(1, 2) });
            put(RoomShape.ROOMSHAPE_LBL, new Vector2i[] { new Vector2i(-1, 0), new Vector2i(0, -1), new Vector2i(2, 0), new Vector2i(0, 1),      new Vector2i(0, 1), new Vector2i(1, -1), new Vector2i(2, 1), new Vector2i(1, 2)});
            put(RoomShape.ROOMSHAPE_LBR, new Vector2i[] { new Vector2i(-1, 0), new Vector2i(0, -1), new Vector2i(2, 0), new Vector2i(0, 2),      new Vector2i(-1, 1),  new Vector2i(1, -1), new Vector2i(1, 1), new Vector2i(1, 1) });
        }
    };
    
    public static Vector2i[][] directionShapeOffsets = new Vector2i[][] {
            // Left
            new Vector2i[] { new Vector2i(-1, 0), new Vector2i(0, 0), new Vector2i(0, -1), new Vector2i(-1, 0), new Vector2i(-1, -1), new Vector2i(-1, 0), new Vector2i(-1, -1), new Vector2i(-1, 0), new Vector2i(-1, -1), new Vector2i(-1, 0), new Vector2i(-1, -1), new Vector2i(0, 0), new Vector2i(-1, 0) },
            // Up
            new Vector2i[] { new Vector2i(0, -1), new Vector2i(0, 0), new Vector2i(-1, 0), new Vector2i(0, -1), new Vector2i(-1, -1), new Vector2i(0, -1), new Vector2i(-1, -1), new Vector2i(0, -1), new Vector2i(-1, -1), new Vector2i(0, -1), new Vector2i(-1, -1), new Vector2i(0, 0), new Vector2i(0, -1) },
            // Right
            new Vector2i[] { new Vector2i(0, 0), new Vector2i(0, 0), new Vector2i(0, -1), new Vector2i(0, 0), new Vector2i(0, -1), new Vector2i(0, 0), new Vector2i(0, -1), new Vector2i(0, 0), new Vector2i(0, -1), new Vector2i(0, 0), new Vector2i(0, -1), new Vector2i(0, 0), new Vector2i(0, 0) },
            // Down
            new Vector2i[] { new Vector2i(0, 0), new Vector2i(0, 0), new Vector2i(-1, 0), new Vector2i(0, 0), new Vector2i(-1, 0), new Vector2i(0, 0), new Vector2i(-1, 0), new Vector2i(0, 0), new Vector2i(-1, 0), new Vector2i(0, 0), new Vector2i(-1, 0), new Vector2i(0, 0), new Vector2i(0, 0) },
    };

    public static RoomShape[][] directionToShape = new RoomShape[][] {
            // Left
            new RoomShape[] { RoomShape.ROOMSHAPE_2x1, RoomShape.ROOMSHAPE_1x2, RoomShape.ROOMSHAPE_1x2, RoomShape.ROOMSHAPE_2x2, RoomShape.ROOMSHAPE_2x2, RoomShape.ROOMSHAPE_LBR, RoomShape.ROOMSHAPE_LTR, RoomShape.ROOMSHAPE_LBL, RoomShape.ROOMSHAPE_LBL, RoomShape.ROOMSHAPE_LTL, RoomShape.ROOMSHAPE_LTL, RoomShape.ROOMSHAPE_IH, RoomShape.ROOMSHAPE_IIH },
            // Up
            new RoomShape[] { RoomShape.ROOMSHAPE_1x2, RoomShape.ROOMSHAPE_2x1, RoomShape.ROOMSHAPE_2x1, RoomShape.ROOMSHAPE_2x2, RoomShape.ROOMSHAPE_2x2, RoomShape.ROOMSHAPE_LBR, RoomShape.ROOMSHAPE_LBL, RoomShape.ROOMSHAPE_LTR, RoomShape.ROOMSHAPE_LTR, RoomShape.ROOMSHAPE_LTL, RoomShape.ROOMSHAPE_LTL, RoomShape.ROOMSHAPE_IV, RoomShape.ROOMSHAPE_IIV },
            // Right
            new RoomShape[] { RoomShape.ROOMSHAPE_2x1, RoomShape.ROOMSHAPE_1x2, RoomShape.ROOMSHAPE_1x2, RoomShape.ROOMSHAPE_2x2, RoomShape.ROOMSHAPE_2x2, RoomShape.ROOMSHAPE_LBL, RoomShape.ROOMSHAPE_LTL, RoomShape.ROOMSHAPE_LTR, RoomShape.ROOMSHAPE_LTR, RoomShape.ROOMSHAPE_LBR, RoomShape.ROOMSHAPE_LBR, RoomShape.ROOMSHAPE_IH, RoomShape.ROOMSHAPE_IIH },
            // Down
            new RoomShape[] { RoomShape.ROOMSHAPE_1x2, RoomShape.ROOMSHAPE_2x1, RoomShape.ROOMSHAPE_2x1, RoomShape.ROOMSHAPE_2x2, RoomShape.ROOMSHAPE_2x2, RoomShape.ROOMSHAPE_LTR, RoomShape.ROOMSHAPE_LTL, RoomShape.ROOMSHAPE_LBL, RoomShape.ROOMSHAPE_LBL, RoomShape.ROOMSHAPE_LBR, RoomShape.ROOMSHAPE_LBR, RoomShape.ROOMSHAPE_IV, RoomShape.ROOMSHAPE_IIV },
    };

    public static int[] shapeWeights = new int[] {
            48, 24, 24, 24, 24, 72, 72, 36, 36, 36, 36, 24, 24
    };

    public static int[] resizeRoomDoorMask = new int[] {
            0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0
    };

    public Vector2i getPos() {
        return pos;
    }

    public static Vector2i getDoorOrigin(Vector2i coords, RoomShape shape, int door) {
        Vector2i[] doors = shapeDoors.get(shape);
        Vector2i off = doors[door];
        if (off == null)
            return null;
        Vector2i dst = new Vector2i(off.getX() + coords.getX(), off.getY() + coords.getY());

        return switch (door) {
            case 0, 4 -> dst.add(1, 0);
            case 1, 5 -> dst.add(0, 1);
            case 2, 6 -> dst.add(-1, 0);
            case 3, 7 -> dst.add(0, -1);
            default -> null;
        };
    }

    public static Vector2i getNeighborCoords(Vector2i coords, RoomShape shape, int door) {
        Vector2i[] doors = shapeDoors.get(shape);
        var off = doors[door];
        if (off == null)
            return null;
        return new Vector2i(coords.getX() + off.getX(), coords.getY() + off.getY());
    }

    public Vector2i getNeighborCoords(int door) {
        return getNeighborCoords(getPos(), getShape(), door);
    }

    public Vector2i getPlacedNeighborCellCoords(int direction) {
        return getNeighborCellCoords(getPlacedPos(), direction);
    }

    public static Vector2i getNeighborCellCoords(Vector2i p, int direction) {
        var doors = shapeDoors.get(RoomShape.ROOMSHAPE_1x1);
        var off = doors[direction];
        if (off == null)
            return null;
        return new Vector2i(off.getX() + p.getX(), off.getY() + p.getY());
    }

    public Vector2i getPlacedPos() {
        return placedPos;
    }

    public void placeInWorld(World world, CommandSender sender) {
        Path path = Path.of("./prefabs/templates/" + getShape().name()+".prefab.json");
        Path floorPrefab = Path.of("./prefabs/templates/" + getShape().name()+"_FLOOR.prefab.json");
        Vector2i size = getRoomShapeSizeScaled(getShape());
        BlockSelection prefab = PrefabStore.get().getPrefab(path);
        Vector3i roomPosition = new Vector3i(getPos().getX()*32 + (size.getX()/2), 250, getPos().getY()*32+(size.getY()/2));
        prefab.place(sender, world, roomPosition, null);

        if (this.prefab == null) {
            BlockSelection customPrefab = PrefabStore.get().getPrefab(floorPrefab);
            customPrefab.place(sender, world, roomPosition, new BlockMask(new BlockFilter[]{new BlockFilter(BlockFilter.FilterType.AboveBlock, new String[]{"Empty"}, true)}));
        }

        //world.setBlock(getPos().getX()*32, 255, getPos().getY()*32, isDeadEnd ? "Cloth_Block_Wool_Red" : (getShape().name().equals("ROOMSHAPE_1x1") ? "Cloth_Block_Wool_Orange_Light" : "Cloth_Block_Wool_Green") );
    }

    private void checkRoomCleared(Store<EntityStore> store) {
        if (hasThreat(store)) return;
        setCleared(true);
        clearRoom(store);
    }

    public void onRoomEntered(Store<EntityStore> store) {
        if (!isCleared())
            convertPrefabIntoPlayableRoom(store.getExternalData().getWorld(), ConsoleSender.INSTANCE);
        checkRoomCleared(store);
    }

    public void onRoomLeave(Store<EntityStore> store) {
        for (Ref<EntityStore> ref : aliveMonsters) {
            if (ref == null || !ref.isValid()) continue;
            IsaacMonsterComponent monsterComponent = store.getComponent(ref, IsaacMonsterComponent.getComponentType());
            if (monsterComponent != null && monsterComponent.getAI().isThreat())
                store.removeEntity(ref, RemoveReason.REMOVE);
        }
    }

    public void convertPrefabIntoPlayableRoom(World world, CommandSender sender) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector2i size = getRoomShapeSizeScaled(getShape());
        Vector3i roomPosition = new Vector3i(getPos().getX()*32 + (size.getX()/2), 250, getPos().getY()*32+(size.getY()/2));

        if (this.prefab != null) {
            this.prefab.place(sender, world, new Vector3i(roomPosition.x, roomPosition.y, getPos().getY() * 32 - 1), new BlockMask(new BlockFilter[]{new BlockFilter(BlockFilter.FilterType.AboveBlock, new String[]{"Empty"}, true)}));

            this.prefab.forEachBlock((x, y, z, blockHolder) -> {
                BlockType blockType = assetMap.getAsset(blockHolder.blockId());
                String blockId = blockType.getId();
                String spawnerId = EntitySpawnUtil.getMonsterIdFromBlock(blockId);
                if (spawnerId != null) {
                    Ref<EntityStore> monsterRef = MonsterCommand.spawnMonster(store, spawnerId, new Vector3d(roomPosition.x + x + 0.5, roomPosition.y + y, getPos().getY() * 32 - 1 + z + 0.5), new Vector3f());
                    if (monsterRef == null) return;
                    this.aliveMonsters.add(monsterRef);
                    world.setBlock(roomPosition.x + x, roomPosition.y + y, getPos().getY() * 32 - 1 + z, "Empty");
                }
                if (blockId.equals("Block_Cliff_Editor")) {
                    world.setBlock(roomPosition.x + x, roomPosition.y + y, getPos().getY() * 32 - 1 + z, "Block_Cliff");
                }
                //TODO: fix closed doors spawning in non neighbour zone, threat zone
                /*if (blockId.equals("DoorClosed_Basement")) {
                    int doorId = RoomRegister.getDoorIdFromCoords(shape, new Vector2i(x, z-16));
                    HytaleLogger.getLogger().atInfo().log("key contained? : "+ neighbors.containsKey(doorId) + ", doorId: " + doorId);
                    if (!neighbors.containsKey(doorId)) world.setBlock(roomPosition.x + x, roomPosition.y + y, getPos().getY() * 32 - 1 + z, "Empty");
                }*/
            });
        }

        // TODO this is hardcoded like many things in this demo, need to be changed
        if (getRoomType() == RoomType.ROOM_TREASURE) {
            int playerCount = Universe.get().getPlayerCount();
            for (int i = 0; i < playerCount; i++) {
                Vector3d pedestalPosition = new Vector3d(roomPosition.x + i + 0.5 - ((double)(playerCount-1)/2), roomPosition.y + 2, getPos().getY()*32-1 + 0.5 + 16);
                ItemUtils.spawnItemPedestal(store, ItemUtils.getRandomItemName(), pedestalPosition);
            }
        } else if (getRoomType() == RoomType.ROOM_SHOP) {
            for (int i = 0; i <= 5; i+=5) {
                Vector3d pedestalPosition = new Vector3d(roomPosition.x + i + 0.5 - 2.5, roomPosition.y + 2, getPos().getY()*32-1 + 0.5 + 15);
                ItemUtils.spawnItemPedestal(store, ItemUtils.getRandomItemName(), pedestalPosition, 15);
            }

            for (int i = 0; i <= 5; i+=5) {
                Vector3d pedestalPosition = new Vector3d(roomPosition.x + i + 0.5 - 2.5, roomPosition.y + 2, getPos().getY()*32-1 + 0.5 + 17);
                if (i == 0) {
                    ItemUtils.spawnItemPedestal(store, "Pickup_Bomb", pedestalPosition, 5);
                } else {
                    ItemUtils.spawnItemPedestal(store, "Pickup_Key", pedestalPosition, 5);
                }
            }
        } else if (getRoomType() == RoomType.ROOM_BOSS) {
            Vector3d bossPosition = new Vector3d(roomPosition.x + 0.5, roomPosition.y + 2, getPos().getY()*32-1 + 0.5 + 15);
            MonsterCommand.spawnMonster(store, "BabyPlum", bossPosition, new Vector3f());
        }
    }

    public static Vector2i getRoomShapeSize(RoomShape shape) {
        return switch (shape) {
            case ROOMSHAPE_NULL, ROOMSHAPE_IV, ROOMSHAPE_IH, ROOMSHAPE_1x1 -> new Vector2i(1, 1);
            case ROOMSHAPE_1x2, ROOMSHAPE_IIV -> new Vector2i(1, 2);
            case ROOMSHAPE_2x1, ROOMSHAPE_IIH -> new Vector2i(2, 1);
            case ROOMSHAPE_2x2, ROOMSHAPE_LTR, ROOMSHAPE_LTL, ROOMSHAPE_LBL, ROOMSHAPE_LBR -> new Vector2i(2, 2);
        };
    }

    // TODO: this function always return (31, 31). check == 1 instead of == 0
    public static Vector2i getRoomShapeSizeScaled(RoomShape shape) {
        Vector2i size = getRoomShapeSize(shape);
        return new Vector2i((size.x == 0) ? 15 : 31, (size.y == 0) ? 15 : 31);
    }

    public static int getOppositeDoorIndex(int doorIndex, Vector2i offset) {
        int id = (doorIndex % 4);
        boolean isOdd = id%2 == 1;
        if (isOdd) {
            return id^2 + ((offset.x == 0) ? 0 : 4);
        }
        return id^2 + ((offset.y == 0) ? 0 : 4);
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomType getRoomType() {
        return this.roomType;
    }

    public void setRoomShape(RoomShape shape) {
        this.shape = shape;
    }

    public void setPos(Vector2i newCoords) {
        this.pos = newCoords;
    }

    public void setRoomPrefab(BlockSelection prefab) {
        this.prefab = prefab;
    }

    public void onMonsterDeath(Store<EntityStore> store, Ref<EntityStore> ref) {
        this.aliveMonsters.removeElement(ref);
        checkRoomCleared(store);
    }

    public void clearRoom(Store<EntityStore> store) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        Vector2i size = getRoomShapeSizeScaled(getShape());
        Vector3i roomPosition = new Vector3i(getPos().getX()*32 + (size.getX()/2), 250, getPos().getY()*32+(size.getY()/2));
        World world = store.getExternalData().getWorld();
        Vector2i[] doorPositions = RoomRegister.shapeDoorLocations.get(shape);

        if (!getPos().equals(new Vector2i(5, 5))) {
            Random random = new Random();
            int dropType = random.nextInt(100);
            // Drop pickups
            if (dropType < 5) { // Drop Chest
                PickupUtils.applyVelocityToPickup(spawnMonster(store, "Pickup_Chest", roomPosition.toVector3d(), new Vector3f()));
            } else if (dropType < 10) { // Drop Locked Chest
                PickupUtils.applyVelocityToPickup(spawnMonster(store, "Pickup_Gold_Chest", roomPosition.toVector3d(), new Vector3f()));
            } else if (dropType < 50) { // Drop Pickups
                int pickupType = random.nextInt(100);
                if (pickupType < 35) { // Coins
                    PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupCoin(store, roomPosition.toVector3d(), new Vector3f()));
                } else if (pickupType < 55) { // Heart
                    PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupHeart(store, roomPosition.toVector3d(), new Vector3f()));
                } else if (pickupType < 70) { // Key
                    PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupKey(store, roomPosition.toVector3d(), new Vector3f()));
                } else { // Bomb
                    PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupBomb(store, roomPosition.toVector3d(), new Vector3f()));
                }
            }
        }

        for (int i = 0; i < doorPositions.length; i++) {
            Vector2i doorPos = doorPositions[i];
            if (doorPos == null) continue;

            Vector3i blockPosition = new Vector3i(roomPosition.getX() + doorPos.getX(), 252, roomPosition.getZ() + doorPos.getY());

            Room neighbour = this.neighbors.get(i);
            if (neighbour == null) continue;

            long chunkIndex = ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z);
            WorldChunk worldChunk = world.getChunk(chunkIndex);

            int blockId = assetMap.getIndex("DoorOpened_Basement");
            BlockType blockType = assetMap.getAsset(blockId);

            int rotation = world.getBlockRotationIndex(blockPosition.x, blockPosition.y, blockPosition.z);
            int filler = worldChunk.getBlockChunk().getSectionAtBlockY(blockPosition.y)
                    .getFiller(blockPosition.x, blockPosition.y, blockPosition.z);

            worldChunk.setBlock(blockPosition.x, blockPosition.y, blockPosition.z, blockId, blockType, rotation, filler, 0);
        }
    }

    public Vector<Ref<EntityStore>> getThreatMonsters() {
        Vector<Ref<EntityStore>> refList = new Vector<>();
        for (Ref<EntityStore> ref : aliveMonsters) {
            if (ref == null || !ref.isValid()) continue;
            Store<EntityStore> store = ref.getStore();
            IsaacMonsterComponent monsterComponent = store.getComponent(ref, IsaacMonsterComponent.getComponentType());
            if (monsterComponent == null) continue;
            if (monsterComponent.getAI().isThreat()) refList.add(ref);
        }
        return refList;
    }
}
