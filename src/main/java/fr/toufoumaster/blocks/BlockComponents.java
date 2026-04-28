package fr.toufoumaster.blocks;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import fr.toufoumaster.TheBindingOfHysaac;

public class BlockComponents {
    public static ComponentType<ChunkStore, RoomManagerBlock> ROOM_MANAGER_TYPE;

    public static void registerComponents(TheBindingOfHysaac plugin) {
        ROOM_MANAGER_TYPE = plugin.getChunkStoreRegistry().registerComponent(RoomManagerBlock.class, "RoomManagerBlock", RoomManagerBlock.CODEC);
    }
}