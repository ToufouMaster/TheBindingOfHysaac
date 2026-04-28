package fr.toufoumaster;

import com.hypixel.hytale.builtin.ambience.resources.AmbienceResource;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.ambiencefx.config.AmbienceFX;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.dungeons.*;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.ui.hud.MinimapHud;
import fr.toufoumaster.utils.CameraUtil;
import fr.toufoumaster.utils.RoomRegister;

import java.util.*;

import static fr.toufoumaster.utils.RoomRegister.*;

public class IsaacGame {

    private static IsaacGame game;
    final static int BUILD_HEIGHT = 250;
    public DungeonLayout generatedLayout;
    public Vector2i curPos;
    Ref<EntityStore> mainMenuRef;

    public IsaacGame() {
        game = this;
        this.generatedLayout  = null;
        this.curPos = null;
        this.mainMenuRef = null;
    }

    public static IsaacGame getInstance() {
        return game;
    }

    public void startGame(Ref<EntityStore> pRef) {
        generatedLayout = generateLayout(pRef);
        curPos = new Vector2i(5, 5);
        buildCurrentLayout(pRef);

        Store<EntityStore> store = pRef.getStore();

        AmbienceFX ambienceFX = AmbienceFX.getAssetMap().getAsset("MusicInnocenceGlitched");
        if (ambienceFX == null) return;
        AmbienceResource ambienceResource = store.getResource(AmbienceResource.getResourceType());
        ambienceResource.setForcedMusicAmbience(ambienceFX.getId());
        store.replaceResource(AmbienceResource.getResourceType(), ambienceResource);

        World world = Universe.get().getDefaultWorld();
        if (this.mainMenuRef != null && this.mainMenuRef.isValid()) store.removeEntity(this.mainMenuRef, RemoveReason.REMOVE);
        for (PlayerRef playerRef : Universe.get().getPlayers()) {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null) continue;
            store.ensureAndGetComponent(ref, IsaacComponent.getComponentType());
            store.putComponent(ref, Teleport.getComponentType(), new Teleport(new Vector3d(game.curPos.getX()*32 + 15.5, 252, game.curPos.getY()*32 + 15.5), new Vector3f()));

            InventoryComponent hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
            if (hotbar == null) return;
            hotbar.getInventory().clear();
            hotbar.getInventory().addItemStack(new ItemStack("IsaacGun", 1));

            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("IsaacModel");
            if (modelAsset == null) return;
            Model model = Model.createScaledModel(modelAsset, 1.0f);

            store.replaceComponent(ref, ModelComponent.getComponentType(), new ModelComponent(model));
            CameraUtil.setCamera(playerRef, RoomShape.ROOMSHAPE_1x1, game.generatedLayout.getRoom(curPos));
        }
        generatedLayout.getRoom(curPos).onRoomEntered(store);

        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        worldTimeResource.setDayTime(0.5, world, store);
    }

    public DungeonLayout generateLayout(Ref<EntityStore> pRef) {
        Store<EntityStore> store = pRef.getStore();
        World world = store.getExternalData().getWorld();
        final int FLOOR_WIDTH = 15;
        final int FLOOR_HEIGHT = 12;

        final int ROOM_SIZE = 16;

        for (int i = 0; i < FLOOR_WIDTH*2; i++) {
            for (int j = 0; j < FLOOR_HEIGHT*2; j++) {
                for (int x = 0; x < ROOM_SIZE; x++) {
                    for (int z = 0; z < ROOM_SIZE; z++) {
                        for (int y = 0; y < 6; y++) {
                            world.setBlock(i * ROOM_SIZE + x, BUILD_HEIGHT + y, j * ROOM_SIZE + z, (y == 0) ? "Rock_Magma_Cooled" : "Empty");
                        }
                    }
                }
            }
        }

        DungeonLayoutGenerator dungeonLayoutGenerator = new DungeonLayoutGenerator();

        DungeonFloor floor = DungeonFloor.Basement;
        int chapter = floor.getChapter();

        int deadEndsMin = 7;
        if (chapter != 1) {
            deadEndsMin += 1;
        }
        /*
        if CurseOfTheLabyrinth then
        MinDeadEnds += 1
        end*/
        if (chapter == 12) {
            deadEndsMin += 2;
        }

        int minDiff;
        int maxDiff;
        if (false) { // if Game.IsHard
            minDiff = chapter < 9 && chapter % 2 == 0 ? 10 : 5;
            maxDiff = chapter < 9 && chapter % 2 == 0 ? 15 : 5;
        } else {
            minDiff = chapter < 9 && chapter % 2 == 0 ? 5 : 1;
            maxDiff = chapter < 9 && chapter % 2 == 0 ? 10 : 5;
        }

        DungeonLayout layout = null;
        for (int i = 0; i < 100; i++) {
            DungeonLayout l = dungeonLayoutGenerator.create();

            if (l.getDeadEnds().size() < deadEndsMin)
                continue;

            if (!placeRooms(l, minDiff, maxDiff))
                continue;

            layout = l;
            break;
        }

        return layout;
    }

    public void buildCurrentLayout(Ref<EntityStore> pRef) {
        Store<EntityStore> store = pRef.getStore();
        // Set HUD Minimap layout
        for (PlayerRef playerRef : Universe.get().getPlayers()) {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null || !ref.isValid()) continue;
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) continue;
            MinimapHud hud = (MinimapHud) player.getHudManager().getCustomHud();
            if (hud == null) continue;
            hud.update(true, new UICommandBuilder());
        }

        for (Room room : generatedLayout.getRoomGrid().values()) {
            room.placeInWorld(store.getExternalData().getWorld(), ConsoleSender.INSTANCE);
        }
    }

    Room popBossDeadEnd(DungeonLayout layout, RoomShape shape) {
        ArrayList<Room> deadEnds = layout.getDeadEnds();

        if (deadEnds.isEmpty())
            return null;

        HashMap<Vector2i, Boolean> hash = new HashMap<>();
        for (Vector2i vector2i : shapeDoorLocations.get(shape)) {
            hash.put(vector2i, true);
        };

        for (var i = 0; i < deadEnds.size(); i++) {
            var de = deadEnds.get(i);
            if (de.distance <= 1) //Ignore rooms attached to the start
                continue;

            /*if (State.IsXL) {
                var coords = de.PlacedCoords;
                var valid = true;
                for (var j = 0; j < 3; j++) {
                    coords = Room.GetNeighborCellCoords(coords, de.DirectionFromParent);
                    if (!StageLayout.InBounds(coords)) {
                        valid = false;
                        break;
                    }
                    var room = layout.GetRoom(coords);
                    if (room != null && room != de) {
                        valid = false;
                        break;
                    }
                    if (layout.GetNeighborCount(coords, de) > 0){
                        valid = false;
                        break;
                    }
                }
                if (!valid)
                    continue;
            }*/

            if (tryResizeRoom(layout, de, shape, doorsToBits(de.getShape(), hash))) {
                deadEnds.remove(de);
                return de;
            }
        }

        return null;
    }

    private boolean tryResizeRoom(DungeonLayout layout, Room deadEnd, RoomShape newShape, int doors) {
        // TODO: get current shape if 2x2 size
        deadEnd.setRoomShape(newShape);
        int dirFrom = deadEnd.getParentDirection();
        Vector<RoomShape> shapes = new Vector<>();
        shapes.add(RoomShape.ROOMSHAPE_1x1);
        shapes.addAll(List.of(Room.directionToShape[dirFrom]));

        List<Integer> doorFlags = Arrays.stream(Room.resizeRoomDoorMask).map(e -> dirFrom + (e * 4)).boxed().toList();
        Vector<Vector2i> dirs = new Vector<>();
        dirs.add(Vector2i.ZERO);
        dirs.addAll(List.of(Room.directionShapeOffsets[dirFrom]));

        for (var i = 0; i < shapes.size(); i++) {
            var canPlace = true;
            var newCoords = deadEnd.getPlacedPos().clone().add(dirs.get(i));

            if (i >= doorFlags.size()) {
                //Console.WriteLine("Hit the bug");
                return false;
            }

            if (shapes.get(i) != newShape)
                continue;

            if ((doors & (1 << doorFlags.get(i))) == 0)
                continue;


            Vector2i[] points = Room.shapeVectors.get(newShape);
            for (var j = 0; j < points.length; j++) {
                Vector2i test = newCoords.add(points[j]);
                if (!DungeonLayout.inBounds(test)) {
                    canPlace = false;
                    break;
                }

                Room room = layout.getRoomGrid().get(test);
                if (room != null && room != deadEnd)
                    canPlace = false;
            }

            int visibleNeighbors = 0;
            for (var door = 0; door < 8; door++) {
                Room neighbor = layout.getRoom(Room.getNeighborCoords(newCoords, newShape, door));
                if (neighbor == null || neighbor == deadEnd)
                    continue;

                visibleNeighbors += 1; //neighbor.isInvisible() ? 0 : 1;

                if ((doors & (1 << door)) != 0)
                    continue;

                canPlace = false;
            }

            if (visibleNeighbors > 1 || !canPlace)
                continue;

            layout.calculateNeighbors(deadEnd);
            layout.reshapeRoom(deadEnd, newShape, newCoords);
            return true;
        }
        return false;
    }

    Room popDeadEnd(DungeonLayout layout, RoomShape shape) {
        ArrayList<Room> deadEnds = layout.getDeadEnds();
        if (deadEnds.isEmpty())
            return null;

        HashMap<Vector2i, Boolean> hash = new HashMap<>();
        for (Vector2i vector2i : shapeDoorLocations.get(shape)) {
            hash.put(vector2i, true);
        };

        for (var i = 0; i < deadEnds.size(); i++) {
            var de = deadEnds.get(i);

            if (tryResizeRoom(layout, de, shape, doorsToBits(de.getShape(), hash))) {
                deadEnds.remove(de);
                return de;
            }
        }

        return null;
    }

    boolean placeRooms(DungeonLayout layout, int minDiff, int maxDiff) {
        Room deadEnd;

        DungeonFloor floor = layout.getFloor();
        if (floor == null) return false;
        int chapter = floor.getChapter();

        Room bossDeadEnd = popBossDeadEnd(layout, RoomShape.ROOMSHAPE_1x1);//layout.getDeadEnds().get(0).get);
        if (bossDeadEnd == null)
            return false;
        bossDeadEnd.setRoomType(RoomType.ROOM_BOSS);


        Room superSecretDeadEnd = popDeadEnd(layout, RoomShape.ROOMSHAPE_1x1);
        if (superSecretDeadEnd == null)
            return false;
        //superSecretDeadEnd.setRoomType(RoomType.ROOM_SUPERSECRET);

        for (int i = 0; i <= 2; i++) {
            Room shopDeadEnd = popDeadEnd(layout, RoomShape.ROOMSHAPE_1x1);
            if (shopDeadEnd == null)
                return false;
            shopDeadEnd.setRoomType(RoomType.ROOM_SHOP);
            shopDeadEnd.setRoomPrefab(getRandomRoomFrom(layout.getRng(), shopDeadEnd.getRoomType(), shopDeadEnd.getShape(), RoomRegister.getRoomDoorMask(shopDeadEnd)));
        }

        for (int i = 0; i <= 3; i++) {
            Room treasureDeadEnd = popDeadEnd(layout, RoomShape.ROOMSHAPE_1x1);
            if (treasureDeadEnd == null)
                return false;
            treasureDeadEnd.setRoomType(RoomType.ROOM_TREASURE);
            treasureDeadEnd.setRoomPrefab(getRandomRoomFrom(layout.getRng(), treasureDeadEnd.getRoomType(), treasureDeadEnd.getShape(), RoomRegister.getRoomDoorMask(treasureDeadEnd)));
        }

        /*
        var bossRoom = chooseBossRoomSubtype(chapter);
        var dt = chooseDoubleTrouble(chapter);

        var roomSeed = new Rng(0); // TODO: Replace with random.nextInt()

        RoomDescriptor boss = tryGetBossRoom(roomSeed, bossRoom, dt);
        if (boss == null) {
            return false;
        }

        // First boss room
        var bossDeadend = PopBossDeadEnd(layout, boss.RoomShape, boss.Doors);
        if (bossDeadend == null)
            return false;
        bossDeadend.ApplyDescriptor(boss, Game.StageSeed); //In isaac this is place_room. Kept layout rooms and game rooms together for simplicity

        // Second boss room
        if (State.IsXL) {
            var origSeed = Game.StageSeed.Clone();
            var secBossRoom = ChooseBossRoomSubtype(chapter+ 1);
            var secDt = ChooseDoubleTrouble(chapter+ 1);
            var secBoss = TryGetBossRoom(Game.StageSeed, secBossRoom, secDt);
            if (secBoss == null)
                return false;
            var secBossDeadEnd = LayoutGen.TryAddBossDeadend(layout, bossDeadend, true);
            if (secBossDeadEnd == null)
                return false; //Couldn't place the second boss room
            if (!TryResizeRoom(layout, secBossDeadEnd, secBoss.RoomShape, secBoss.Doors))
                return false;

            secBossDeadEnd.ApplyDescriptor(secBoss, Game.StageSeed);
            Game.StageSeed = origSeed;
        }

        // Super Secret
        {
            var superSecret = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, RoomType.ROOM_SUPERSECRET, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 10, 0, -1);
            if ((deadEnd = PopDeadEnd(layout, superSecret.RoomShape, superSecret.Doors)) == null)
                return false;
            deadEnd.ApplyDescriptor(superSecret, Game.StageSeed);
            deadEnd.Invisible = true; //Todo: Check that this actually gets set
        }

        var secretSeed = new Rng(Game.StageSeed.Next(), 0x1, 0x5, 0x10);

        // Shop
        if (chapter< 7 || (chapter< 9 && Game.Trinkets[0x6e])) {
            var shopSubtype = 4; //Based on unlocks
            var num = (Game.StageSeed.Next() & 0xFF);
            if (num == 0)
                shopSubtype = 0xB;
            else if (num == 1)
                shopSubtype = 0xA;

            var shop = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, RoomType.ROOM_SHOP, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 10, 0, shopSubtype);
            if ((deadEnd = PopDeadEnd(layout, shop.RoomShape, shop.Doors)) == null)
                return false;
            deadEnd.ApplyDescriptor(shop, Game.StageSeed);
        }

        // Treasure
        if (chapter< 7 || (chapter< 9 && Game.Trinkets[0x6f])) {
            var treasureCount = State.IsXL ? 2 : 1;
            var seed = Game.StageSeed;
            for (var i = 0; i < treasureCount; i++) {
                int trSubType;
                var hsChance = Game.ActiveItem == 0x1b7 ? 0x32 : 0x64;
                //Golden Horseshoe
                if (seed.NextInt(100) == 0 || (seed.NextInt(hsChance) < 15 && Game.Trinkets[0x52])) {
                    //Pay To Win
                    trSubType = Game.Trinkets[0x70] ? 3 : 1;

                } else {
                    //Pay To Win
                    trSubType = Game.Trinkets[0x70] ? 2 : 0;
                }

                var treasure = Provider.GetRandomRoom(seed.Next(), true, 0, RoomType.ROOM_TREASURE, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 10, 0, trSubType);
                if ((deadEnd = PopDeadEnd(layout, treasure.RoomShape, treasure.Doors)) == null)
                    return false;
                deadEnd.ApplyDescriptor(treasure, seed);
                seed = seed.Clone();
            }
        }

        // Dice/Sacrifice
        if (chapter< 0xB) {
            {
                var roomType = Game.StageSeed.NextInt(0x32) == 0 || (Game.StageSeed.NextInt(0x5) == 0 && Game.Keys > 1)
                        ? RoomType.ROOM_DICE
                        : RoomType.ROOM_SACRIFICE;
                var room = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, roomType, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 0xa, 0, -1);
                //Bug? Resizes the dead end before determining if the room will be placed
                deadEnd = PopDeadEnd(layout, room.RoomShape, room.Doors);
                if (Game.StageSeed.NextInt(0x7) == 0 || ((Game.StageSeed.Next() & 3) == 0 && Game.Hearts + Game.SoulHearts >= Game.MaxHearts)) {
                    deadEnd?.ApplyDescriptor(room, Game.StageSeed);
                } else {
                    layout.ReaddDeadEnd(deadEnd); //Dead end gets readded at the end
                }
            }

            // Library
            {
                var libSubtype = 4; //Based on unlocks
                var room = Provider.GetRandomRoom(Game.StageSeed.Next(), false, 0, RoomType.ROOM_LIBRARY, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 0xa, 0, Game.StageSeed.NextInt(libSubtype + 1));
                deadEnd = PopDeadEnd(layout, room.RoomShape, room.Doors);
                if (Game.StageSeed.NextInt(0x14) == 0 || ((Game.StageSeed.Next() & 3) == 0 && Game.GetFlag(GameStateFlags.STATE_BOOK_PICKED_UP))) {
                    deadEnd?.ApplyDescriptor(room, Game.StageSeed);
                } else {
                    layout.ReaddDeadEnd(deadEnd); //Dead end gets readded at the end
                }
            }

            // Curse
            {
                var room = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, RoomType.ROOM_CURSE, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 0xa, 0, -1);
                deadEnd = PopDeadEnd(layout, room.RoomShape, room.Doors);
                if ((Game.StageSeed.Next() & 1) == 0 || ((Game.StageSeed.Next() & 3) == 0 && Game.GetFlag(GameStateFlags.STATE_DEVILROOM_VISITED))) {
                    deadEnd?.ApplyDescriptor(room, Game.StageSeed);
                } else {
                    layout.ReaddDeadEnd(deadEnd); //Dead end gets readded at the end
                }
            }

            // Mini Boss
            {
                uint variant = uint.MaxValue;
                GameStateFlags flags = 0;
                if (chapter> 2 && Game.StageSeed.NextInt(0xA) == 0 && !Game.GetFlag(GameStateFlags.STATE_ULTRAPRIDE_SPAWNED)) {
                    Game.StageSeed.Next(); //Unused
                    variant = 0x8D4;
                    flags = GameStateFlags.STATE_ULTRAPRIDE_SPAWNED;
                } else {
                    var isAlt = Game.StageSeed.NextInt(0x5) == 0; //Based on unlocks
                    var miniBosses = MiniBosses.Where(t => !Game.GetFlag(t.Item1)).ToArray();
                    var randMiniBossIdx = miniBosses.Length > 0 ? Game.StageSeed.NextInt(miniBosses.Length) : -1;
                    if (randMiniBossIdx != -1) {
                        var randMiniboss = miniBosses[randMiniBossIdx];
                        variant = isAlt ? randMiniboss.Item3 : randMiniboss.Item2;
                        flags = randMiniboss.Item1;
                    }
                }
                if (variant != uint.MaxValue) {
                    var room = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, RoomType.ROOM_MINIBOSS, RoomShape.NUM_ROOMSHAPES, variant, variant + 0x9, 1, 0xa, 0, -1);
                    deadEnd = room != null ? PopDeadEnd(layout, room.RoomShape, room.Doors) : null;
                    if (((Game.StageSeed.Next() & 3) == 0 || (Game.StageSeed.NextInt(3) == 0 && chapter== 1)) && deadEnd != null) {
                        deadEnd?.ApplyDescriptor(room, Game.StageSeed);
                        Game.SetFlag(flags, true);
                    } else {
                        layout.ReaddDeadEnd(deadEnd); //Dead end gets readded at the end
                    }
                } else {
                    //Bug: The dead end placed before MiniBoss gets placed again. TBD Does this have any side effects?
                }
            }

            // Challenge
            {
                var room = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, RoomType.ROOM_CHALLENGE, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 0xa, 0, -1);
                deadEnd = PopDeadEnd(layout, room.RoomShape, room.Doors);
                if (((Game.StageSeed.Next() & 1) == 0 || chapter> 2) && Game.Hearts + Game.SoulHearts >= Game.MaxHearts && chapter> 1) {
                    deadEnd?.ApplyDescriptor(room, Game.StageSeed);
                } else {
                    layout.ReaddDeadEnd(deadEnd); //Dead end gets readded at the end
                }
            }

            // Chest and Arcade

            {
                var roomType = Game.StageSeed.NextInt(0xA) == 0 || (Game.StageSeed.NextInt(0x3) == 0 && Game.Keys > 1)
                        ? RoomType.ROOM_CHEST
                        : RoomType.ROOM_ARCADE;
                var room = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, roomType, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 0xa, 0, -1);
                deadEnd = PopDeadEnd(layout, room.RoomShape, room.Doors);
                if (Game.Coins >= 5 && chapter% 2 == 0 && chapter< 9) {
                    deadEnd?.ApplyDescriptor(room, Game.StageSeed);
                } else {
                    layout.ReaddDeadEnd(deadEnd); //Dead end gets readded at the end
                }
            }
        }

        // Isaacs/Barren
        if (chapter< 7) {
            var roomType = (Game.StageSeed.Next() & 1) == 0
                    ? RoomType.ROOM_ISAACS
                    : RoomType.ROOM_BARREN;
            //From the wiki. Didn't double check
            var maxHearts = 0;
            if (Game.Character == PlayerType.PLAYER_THELOST || Game.Character == PlayerType.PLAYER_XXX || Game.Character == PlayerType.PLAYER_THESOUL) {
                maxHearts = Game.MaxHearts;
            } else {
                maxHearts = Game.MaxHearts + Game.BoneHearts * 2;
            }
            var room = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, roomType, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 0xa, 0, -1);
            deadEnd = PopDeadEnd(layout, room.RoomShape, room.Doors);
            if (Game.StageSeed.NextInt(0x32) == 0 || (Game.StageSeed.NextInt(0x5) == 0 && ((Game.Hearts < 2 && Game.SoulHearts == 0) || (maxHearts == 0 && Game.SoulHearts <= 2)))) {
                deadEnd?.ApplyDescriptor(room, Game.StageSeed);
            } else {
                layout.ReaddDeadEnd(deadEnd); //Dead end gets readded at the end
            }
        }

        // Secret
        {
            var secretCount = Game.Trinkets[0x66] ? 2 : 1;
            for (var i = 0; i < secretCount; i++) {
                var room = Provider.GetRandomRoom(secretSeed.Seed, true, 0, RoomType.ROOM_SECRET, RoomShape.NUM_ROOMSHAPES, 0, uint.MaxValue, 1, 10, 0, -1);
                var blacklist = CalculateSecretRoomBlacklist(layout);
                var secret = CreateSecretRoom(layout, blacklist);
                if (secret != null) {
                    secret.ApplyDescriptor(room, new Rng(secretSeed.Seed, Game.StageSeed.Shift1, Game.StageSeed.Shift2, Game.StageSeed.Shift3));
                }
                secretSeed.Next();
            }
        }

        //Grave
        if (chapter== 0xB && Game.StageVariant == 0) {
            var room = Provider.GetRandomRoom(Game.StageSeed.Next(), true, 0, RoomType.ROOM_DEFAULT, RoomShape.NUM_ROOMSHAPES, 3, 9, 1, 0xa, 0, -1);
            deadEnd = PopDeadEnd(layout, room.RoomShape, room.Doors);
            if (deadEnd == null)
                return false;
            deadEnd?.ApplyDescriptor(room, Game.StageSeed);
        }

        var stageId = Game.StageId;
        var normalRooms = layout.Rooms.Where(r => !r.IsDeadEnd).Concat(layout.DeadEnds);
        foreach (var room in normalRooms) {
            if (room.RoomType != RoomType.ROOM_NULL) //Ignore placed rooms
                continue;

            RoomDescriptor rd;
            if (room.RoomX == 6 && room.RoomY == 6 && room.Distance == 0) {
                if (chapter== 0xB && Game.StageVariant == 0) {
                    rd = Provider.StageRooms[0x10].First(e => e.RoomId == 0);
                } else if (chapter== 0xB && Game.StageVariant == 1) {
                    rd = Provider.StageRooms[0x11].First(e => e.RoomId == 0);
                } else {
                    rd = Provider.StageRooms[0].First(e => e.RoomId == 2);
                }
            } else {
                var doors = layout.CalculateDoorBits(room);
                var minVar = chapter== 0xB ? 1u : 0; //Don't include the start rooms
                rd = Provider.GetRandomRoom(Game.StageSeed.Next(), true, stageId, RoomType.ROOM_DEFAULT, room.Shape, minVar, uint.MaxValue, minDiff, maxDiff, doors, -1);
            }

            if (rd == null) {
                //Console.WriteLine("Couldn't find required room");
                return false;
            }

            room.ApplyDescriptor(rd, Game.StageSeed);

        }*/


        return true;
    }

    public boolean isRunning() {
        return (curPos != null && generatedLayout != null);
    }

    public void setMainMenuRef(Ref<EntityStore> entityStoreRef) {
        this.mainMenuRef = entityStoreRef;
    }

    public Ref<EntityStore> getMainMenuRef() {
        return this.mainMenuRef;
    }
}
