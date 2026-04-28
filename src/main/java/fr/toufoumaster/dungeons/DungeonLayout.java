package fr.toufoumaster.dungeons;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;

public class DungeonLayout {

    DungeonFloor floorData;
    ArrayList<Room> rooms;
    ArrayList<Room> deadEnds;
    HashMap<Vector2i, Room> roomGrid;
    Rng rng = null;

    DungeonLayout() {
        rooms = new ArrayList<>();
        roomGrid = new HashMap<>();
    }

    public static DungeonLayout fromFloor(DungeonFloor dungeonFloor) {
        DungeonLayout dungeonLayout = new DungeonLayout();

        dungeonLayout.floorData = dungeonFloor;

        return dungeonLayout;
    }

    public void setRng(Rng rng) {
        this.rng = rng;
    }

    public Rng getRng() {
        return rng;
    }

    public Room placeRoom(Room room) {
        room.id = rooms.size();
        var points = Room.shapeVectors.get(room.getShape());
        for (Vector2i point : points) {
            var p = new Vector2i(point.getX() + room.getPos().getX(), point.getY() + room.getPos().getY());
            if (!DungeonLayout.inBounds(p))
                continue;

            roomGrid.put(p, room);
        }
        rooms.add(room);
        return room;
    }

    public void reshapeRoom(Room room, RoomShape newShape, Vector2i newCoords) {
        Vector2i[] oldPoints = Room.shapeVectors.get(room.getShape());
        //Remove old shape from grid
        for (int i = 0; i < oldPoints.length; i++) {
            Vector2i p = room.getPos().clone().add(oldPoints[i]);
            if (!inBounds(p)) {
                continue;
            }

            getRoomGrid().remove(p);
        }

        //Add new shape to grid
        var newPoints = Room.shapeVectors.get(newShape);
        for (var i = 0; i < newPoints.length; i++) {
            var p = newCoords.clone().add(newPoints[i]);
            if (!inBounds(p)) {
                continue;
            }

            getRoomGrid().put(p, room);
        }
        room.setPos(newCoords);
        room.setRoomShape(newShape);
    }

    public void calculateNeighbors(Room room) {
        room.setNeighbours(new HashMap<>());
        for (int j = 0; j < 8; j++) {
            Vector2i coords = room.getNeighborCoords(j);
            if (coords == null) continue;
            Room neighbor = getRoom(coords);
            if (neighbor != null)
                room.getNeighbors().put(j, neighbor);
        }
    }

    public int getNeighborCount(Vector2i p, Room ignore) {
        int num = 0;
        var dirs = Room.shapeDoors.get(RoomShape.ROOMSHAPE_1x1);
        for (var i = 0; i < 4; i++) {
            var t = new Vector2i(p.getX() + dirs[i].getX(), p.getY() + dirs[i].getY());
            var n = getRoom(t);
            if (inBounds(t) && n != null && (ignore != null && n != ignore))
                num++;
        }
        return num;
    }

    public int getNeighborCount(Room r) {
        return getNeighborCount(r.getPlacedPos(), r);
    }

    public ArrayList<Room> getDeadEnds() {
        return deadEnds;
    }

    public Room getRoom(Vector2i p) {
        if (p == null) return null;
        if (p.getX() < 0 || p.getY() < 0 || p.getX() >= 13 || p.getY() >= 13)
            return null;

        return roomGrid.get(p);
    }

    public static boolean inBounds(Vector2i p) {
        return p.getX() >= 0 && p.getY() >= 0 && p.getX() < 13 && p.getY() < 13;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public HashMap<Vector2i, Room> getRoomGrid() {
        return roomGrid;
    }

    public DungeonFloor getFloor() {
        return floorData;
    }
}
