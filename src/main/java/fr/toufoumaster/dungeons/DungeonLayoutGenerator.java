package fr.toufoumaster.dungeons;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import fr.toufoumaster.utils.RoomRegister;

import java.util.ArrayList;
import java.util.List;

// Dungeon generator code largely come from bladecoding amazing reverse engineering work on The Binding of Isaac.
// https://github.com/bladecoding/isaac-levelgen/blob/master/isaac-levelgen/

public class DungeonLayoutGenerator {

    Rng random;

    public DungeonLayout create() {
        random = new Rng();
        DungeonFloor floor = DungeonFloor.Basement;

        int numberOfRooms = Math.min(30, random.nextInt(10) + 20);//(int) Math.min(20, random.nextInt(2) + 5 + Math.floor(floor.getChapter() * 10 / 3.0));

        int deadEndsMin = 5;
        if (floor.getChapter() != 1) {
            deadEndsMin += 1;
        }

        if (floor.getChapter() == 12) {
            deadEndsMin += 2;
        }

        DungeonLayout layout = generate(numberOfRooms);
        layout.setRng(random);
        layout.floorData = floor;

        calculateDeadEnds(layout);
        if (layout.deadEnds.size() < 5) {
            for (var i = 0; i < 5; i++) {
                addDeadEnd(layout);
                calculateDeadEnds(layout);
                if (layout.deadEnds.size() >= 5)
                    break;
            }

        }

        layout.rooms.forEach((room -> {
            if (room.getPos().equals(new Vector2i(5, 5))) return;
            BlockSelection prefab = RoomRegister.getRandomRoomFrom(getRng(), room.getRoomType(), room.getShape(), RoomRegister.getRoomDoorMask(room));
            room.setRoomPrefab(prefab);
        }));

        sortList(layout.deadEnds); //Isaac doesn't use a stable sort

        return layout;

        // https://gist.github.com/bladecoding/d75aef7e830c738ad5e3d66d146a095c
    }

    void addDeadEnd(DungeonLayout layout) {
        for (var i = 0; i < layout.rooms.size(); i++) {
            var room = layout.rooms.get(i);
            if (tryAddDeadend(layout, room, false) != null)
                return;
        }
    }

    public Room tryAddDeadend(DungeonLayout layout, Room room, boolean shuffle) {
        List<Room> cands = getNeighborCandidates(layout, room, true);
        if (shuffle) {
           random.shuffle(cands);
        }
        for (Room cand : cands) {
            if (doesRoomShapeFit(layout, cand.getPos(), cand.getShape()) && layout.getNeighborCount(cand) < 2) {
                return layout.placeRoom(cand);
            }
        }
        return null;
    }

    static void calculateDeadEnds(DungeonLayout layout) {
        //Start room cannot be a dead end
        for (int i = 1; i < layout.rooms.size(); i++)
            layout.rooms.get(i).isDeadEnd = layout.getNeighborCount(layout.rooms.get(i)) < 2;

        for (int i = 1; i < layout.rooms.size(); i++) {
            Room room = layout.rooms.get(i);
            var dirToParent = (room.getParentDirection() + 2) % 4;
            var parent = layout.getRoom(room.getPlacedNeighborCellCoords(dirToParent));
            if (parent != null)
                parent.isDeadEnd = false;
        }

        for (var i = 0; i < layout.rooms.size(); i++) {
            layout.calculateNeighbors(layout.rooms.get(i));
        }

        layout.deadEnds = new ArrayList<>(layout.rooms.stream().filter(r -> r.isDeadEnd).toList());
    }

    static void sortList(List<Room> list) {
        for (int i = 0; i < list.size(); i++) {
            var sIdx = i;
            for (var j = i + 1; j < list.size(); j++) {
                if (list.get(sIdx).distance < list.get(j).distance)
                    sIdx = j;
            }
            var t = list.get(sIdx);
            list.set(sIdx, list.get(i));
            list.set(i, t);
        }
    }

    public DungeonLayout generate(int maxRooms) {
        int placed = 0;
        DungeonLayout layout = new DungeonLayout();
        ArrayList<Room> rooms = new ArrayList<>();

        //Place start room. Doesn't count towards placed count
        Room startRoom = layout.placeRoom(new Room(RoomShape.ROOMSHAPE_1x1, new Vector2i(5, 5), -1));
        startRoom.setCleared(true);
        rooms.add(startRoom);

        while (!rooms.isEmpty() && placed < maxRooms) {
            Room next = rooms.removeLast();
            List<Room> cands = getNeighborCandidates(layout, next, false);
            random.shuffle(cands);

            int candsToPlace = 0;

            if (startRoom.getPos() == next.getPos()) {
                if (maxRooms > 15)
                    candsToPlace = cands.size();
                else
                    candsToPlace = 2 + (random.nextInt() & 1);
            } else {
                for (int i = 0; i < 4; i++)
                    candsToPlace += random.nextInt() & 1;
                candsToPlace = Math.min(candsToPlace, cands.size());
            }

            for (int i = 0; i < cands.size(); i++) {
                Room cand = cands.get(i);
                if (!doesRoomShapeFit(layout, cand.getPos(), cand.getShape())) {
                    continue;
                }

                var neighborCells = layout.getNeighborCount(cand);
                if (neighborCells < 2 || /*((State.IsXL || State.IsVoid) &&*/ random.nextInt(10) == 0/*)*/) {
                    if (!isShapeLocValid(layout, cand.getPos(), cand.getShape())) {
                        continue;
                    }

                    Room r = layout.placeRoom(cand);
                    placed++;
                    if (random.nextInt(3) == 0) {
                        rooms.addLast(r);
                    } else {
                        rooms.addFirst(r);
                    }
                    if (--candsToPlace <= 0 || placed >= maxRooms)
                        break;
                }
            }
        }

        return layout;
    }

    static boolean isShapeLocValid(DungeonLayout layout, Vector2i origin, RoomShape shape) {
        //testShape is used to get neighboring cells for shapes that are missing doors.
        var testShape = shape;
        if (testShape == RoomShape.ROOMSHAPE_IH)
            testShape = RoomShape.ROOMSHAPE_1x1;
        else if (testShape == RoomShape.ROOMSHAPE_IIH)
            testShape = RoomShape.ROOMSHAPE_2x1;
        else if (testShape == RoomShape.ROOMSHAPE_IV)
            testShape = RoomShape.ROOMSHAPE_1x1;
        else if (testShape == RoomShape.ROOMSHAPE_IIV)
            testShape = RoomShape.ROOMSHAPE_1x2;

        for(var door = 0; door < 8; door++) {
            Vector2i nbCoords = Room.getNeighborCoords(origin, testShape, door);

            if (nbCoords == null)
                continue;

            if (!DungeonLayout.inBounds(nbCoords))
                continue;

            Room neighbor = layout.getRoom(nbCoords);
            if (neighbor == null)
                continue;

            Vector2i src = Room.getDoorOrigin(origin, shape, door);
            if (src == null)
                return false;

            boolean hasConnDoor = false;
            for (int nbDoor = 0; nbDoor < 8; nbDoor++) {
                Vector2i coords = neighbor.getNeighborCoords(nbDoor);
                if (coords != null && coords.equals(src)) {
                    hasConnDoor = true;
                    break;
                }
            }
            if (!hasConnDoor)
                return false;
        }
        return true;
    }

    public boolean doesRoomShapeFit(DungeonLayout layout, Vector2i origin, RoomShape shape) {
        Vector2i roomPos = layout.rooms.getFirst().getPos();
        Vector2i megaCoords = new Vector2i(roomPos.getX(), roomPos.getY()-1);

        Vector2i[] points = Room.shapeVectors.get(shape);
        for (int i = 0; i < points.length; i++) {
            Vector2i test = new Vector2i(origin.getX()+points[i].getX(), origin.getY()+points[i].getY());
            if (!DungeonLayout.inBounds(test))
                return false;

            /*
            if (State.MegaSatanDoorExists && test == megaCoords)
                return false;*/
            if (layout.roomGrid.get(test) != null)
                return false;
        }
        return true;
    }

    List<Room> getNeighborCandidates(DungeonLayout layout, Room room, boolean enableAll) {
        ArrayList<Room> rooms = new ArrayList<>();
        for (int door = 0; door < 8; door++) {
            Vector2i nbCoords = room.getNeighborCoords(door);
            if (nbCoords == null) //Door doesn't exist
                continue;

            if (!DungeonLayout.inBounds(nbCoords)) {
                continue;
            }

            if (!doesRoomShapeFit(layout, nbCoords, RoomShape.ROOMSHAPE_1x1)) {
                continue;
            }

            int direction = door % 4;
            RoomShape[] shapes = Room.directionToShape[direction];
            Vector2i[] offsets = Room.directionShapeOffsets[direction];

            if (true) {//(State.IsShapeEnabled(RoomShape.ROOMSHAPE_1x1)) TODO: implement this
                rooms.add(new Room(RoomShape.ROOMSHAPE_1x1, nbCoords, nbCoords, direction, room.distance + 1));
            }


            for (int i = 0; i < RoomShape.values().length; i++) {
                var candidate = false;
                if (Room.shapeWeights[i] != 0 && random.nextInt(Room.shapeWeights[i]) == 0)
                    candidate = true;
                else if (/*!State.IsShapeEnabled(RoomShape.ROOMSHAPE_1x1) || TODO: implement this*/enableAll)
                    candidate = true;

                var offset = new Vector2i(nbCoords.getX() + offsets[i].getX(), nbCoords.getY() + offsets[i].getY());
                var shape = shapes[i];
                if (/*State.IsShapeEnabled(shape) && TODO: implement this*/candidate && doesRoomShapeFit(layout, offset, shape))
                    rooms.add(new Room(shape, offset, nbCoords, direction, room.distance + 1));
            }
        }
        return rooms;
    }

    public Rng getRng() {
        return random;
    }
}
