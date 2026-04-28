package fr.toufoumaster.ui.pages;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaver;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaverSettings;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import fr.toufoumaster.blocks.RoomManagerBlock;
import fr.toufoumaster.dungeons.RoomShape;
import fr.toufoumaster.utils.RoomRegister;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class RoomManagerActivePage extends InteractiveCustomUIPage<RoomManagerActivePage.PageEventData> {
    Vector3i blockPos;

    public RoomManagerActivePage(@Nonnull PlayerRef playerRef, Vector3i blockPos) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageEventData.CODEC);
        this.blockPos = blockPos;
    }

    public void build(@Nonnull Ref ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store store) {
        commandBuilder.append("Pages/RoomManager.ui");
        for (int i = 1; i < 13; i++) {
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Templates[" + i + "]", (new EventData()).append(PageEventData.KEY_ACTION, PageAction.LoadTemplate).append(PageEventData.KEY_ID, String.valueOf(i)));
        }
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#RoomName", (new EventData()).append(PageEventData.KEY_ACTION, PageAction.SaveRoomData).append(PageEventData.KEY_NAME, "#RoomName.Value").append(PageEventData.KEY_ROOM_TYPE, "#RoomType.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#RoomType", (new EventData()).append(PageEventData.KEY_ACTION, PageAction.SaveRoomData).append(PageEventData.KEY_NAME, "#RoomName.Value").append(PageEventData.KEY_ROOM_TYPE, "#RoomType.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", (new EventData()).append(PageEventData.KEY_ACTION, PageAction.SaveCustom).append(PageEventData.KEY_NAME, "#RoomName.Value").append(PageEventData.KEY_ROOM_TYPE, "#RoomType.Value"));

        Player player = (Player)store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        World world = player.getWorld();
        if (world == null) return;
        RoomManagerBlock roomManagerBlock = getBlockComponent(world);
        if (roomManagerBlock == null) return;
        commandBuilder.set("#RoomType.Value", roomManagerBlock.getType());
        commandBuilder.set("#RoomName.Value", roomManagerBlock.getName());
        commandBuilder.set("#RoomIcon.Background", "Hud/MiniMapAssets/"+ roomManagerBlock.getShape().getId()+".png");
        updateCustoms(commandBuilder, eventBuilder);
    }

    private void updateCustoms(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder) {
        ArrayList<String> prefabFiles = new ArrayList<>();
        final Path prefabStorePath = PrefabStore.get().getServerPrefabsPath();
        commandBuilder.clear("#Custom");
        if (Files.isDirectory(prefabStorePath)) {
            try {
                Files.walkFileTree(prefabStorePath, FileUtil.DEFAULT_WALK_TREE_OPTIONS_SET, Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                    @Nonnull
                    public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                        String fileName = file.getFileName().toString();
                        String parentPath = file.getParent().toString();
                        /*if (!parentPath.endsWith("prefabs") && !parentPath.endsWith("templates")) {
                            parentPath.replace("prefabs\\", "");
                        }*/
                        if (fileName.endsWith(".prefab.json") && !fileName.startsWith("ROOMSHAPE_")) {
                            int id = prefabFiles.size();
                            String filePath = PathUtil.relativize(prefabStorePath, file).toString();
                            prefabFiles.add(filePath);
                            commandBuilder.append("#Custom", "Pages/SavedRoom.ui");
                            commandBuilder.set("#Custom["+id+"] #RoomName.Text", fileName);
                            for (RoomShape shape : RoomShape.values()) {
                                if (!fileName.endsWith(shape.name()+".prefab.json")) continue;
                                commandBuilder.insertBeforeInline("#Custom["+id+"] #RoomName", """
                                        AssetImage #Img {
                                            Anchor: (Width: 96, Height: 96);
                                            AssetPath: "UI/Custom/Hud/MiniMapAssets/"""+shape.getId()+".png\";}");
                                eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Custom["+id+"] #Edit", EventData.of(PageEventData.KEY_ID, filePath).append(PageEventData.KEY_ACTION, PageAction.LoadCustom));

                                eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Custom["+id+"] #Delete", EventData.of(PageEventData.KEY_ID, filePath).append(PageEventData.KEY_ACTION, PageAction.DeleteCustom));
                            }
                        }

                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException _) {
            }
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        Player player = (Player)store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            this.sendUpdate();
            return;
        }

        switch (data.action) {
            case PageAction.LoadTemplate:
                int id = Integer.parseInt(data.id);
                this.sendUpdate(loadTemplate(player, RoomShape.values()[id]));
                return;
            case PageAction.LoadCustom:
                RoomShape shape = null;
                for (RoomShape s : RoomShape.values()) {
                    if (!data.id.endsWith("_"+s.name()+".prefab.json")) continue;
                    shape = s;
                }
                this.sendUpdate(loadCustom(player, data.id, shape));
                if (shape == null) return;
                return;
            case PageAction.SaveCustom:
                String path = data.id;
                if (!data.roomType.isEmpty()) path = data.roomType + "/" + data.id;
                this.sendUpdate(saveCustom(player, path));
                return;
            case PageAction.DeleteCustom:
                this.sendUpdate(deleteCustom(data.id));
                return;
            case PageAction.SaveRoomData:
                updateBlockData(player, data.id, data.roomType);
                this.sendUpdate(new UICommandBuilder());
                return;
        }
        this.sendUpdate();
    }

    public RoomManagerBlock getBlockComponent(World world) {
        if (world == null) return null;
        WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(blockPos.getX(), blockPos.getZ()));
        if (chunk == null) return null;
        Ref<ChunkStore> blockRef = chunk.getBlockComponentEntity(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (blockRef == null || !blockRef.isValid()) return null;
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
        return chunkStore.getComponent(blockRef, RoomManagerBlock.getComponentType());
    }

    private void updateBlockData(Player player, String id, String roomType) {
        World world = player.getWorld();
        if (world == null) return;
        RoomManagerBlock roomManagerBlock = getBlockComponent(world);
        if (roomManagerBlock == null) return;
        if (id != null) roomManagerBlock.setName(id);
        if (roomType != null) roomManagerBlock.setType(roomType);
    }

    private UICommandBuilder deleteCustom(String id) {
        String path = PrefabStore.get().getServerPrefabsPath()+"/"+id;
        try {
            Files.delete(Path.of(path));
        } catch (IOException _) {}
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        updateCustoms(commandBuilder, eventBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
        return new UICommandBuilder();
    }

    private UICommandBuilder saveCustom(Player player, String id) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        World world = player.getWorld();
        if (world == null) return commandBuilder;

        RoomManagerBlock roomManagerBlock = getBlockComponent(world);
        if (roomManagerBlock == null) return commandBuilder;
        RoomShape shape = roomManagerBlock.getShape();

        Vector3i minAnchor, maxAnchor;
        switch (shape) {
            case ROOMSHAPE_1x1, ROOMSHAPE_IH, ROOMSHAPE_IV -> {
                minAnchor = new Vector3i(-7, 1, 9).add(blockPos);
                maxAnchor = new Vector3i(7, 2, 23).add(blockPos);
            }
            default -> {
                minAnchor = new Vector3i(-15, 1, 1).add(blockPos);
                maxAnchor = new Vector3i(15, 2, 31).add(blockPos);
            }
        }

        PrefabSaverSettings settings = new PrefabSaverSettings();
        //settings.setEmpty(true);
        settings.setOverwriteExisting(true);
        settings.setBlocks(true);
        settings.setRelativize(true);
        PrefabSaver.savePrefab(player, world, Path.of("prefabs/"+id + "_" + shape.name() + ".prefab.json"), blockPos, minAnchor, maxAnchor, new Vector3i(), new Vector3i(), settings);
        //PrefabStore.get().saveServerPrefab(, blockSelection, true);
        updateCustoms(commandBuilder, eventBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
        return new UICommandBuilder();
    }

    private UICommandBuilder loadTemplate(Player player, RoomShape shape) {
        UICommandBuilder ui = new UICommandBuilder();

        // Generate template room placement in world
        BlockSelection selection = PrefabStore.get().getServerPrefab("templates/"+shape.name()+"_FLOOR.prefab.json");
        World world = player.getWorld();
        if (world == null) return ui;

        // Clear
        BlockTypeAssetMap<String, BlockType> blockAssetMap = BlockType.getAssetMap();
        world.execute(() -> {
            for (int i = 0; i < 31; i++) {
                for (int j = 0; j < 31; j++) {
                    for (int k = 0; k < 3; k++) {
                        world.setBlock(blockPos.x+i-15, blockPos.y+k, blockPos.z+j+1, "Empty");
                    }
                }
            }

            selection.place(player, world, new Vector3i(0, 0, 16).add(blockPos), null);//.add(blockPos), null);
            /*for (Vector2i pos : RoomRegister.shapeDoorLocations.get(shape)) {
                if (pos == null) continue;
                HytaleLogger.getLogger().atInfo().log(String.valueOf(new Vector3i(blockPos.x+pos.x, blockPos.y+5, blockPos.z+pos.y + 16)));
                world.setBlock(blockPos.x+pos.x, blockPos.y+5, blockPos.z+pos.y + 16, "Cloth_Block_Wool_Blue");
            }*/
            //selection.place(player, world, new Vector3i(15, 0, 12).add(blockPos), null);
        });
        RoomManagerBlock roomManagerBlock = getBlockComponent(world);
        if (roomManagerBlock == null) return ui;
        roomManagerBlock.setShape(shape);
        roomManagerBlock.setName("");

        // Set block data into page data
        ui.set("#RoomName.Value", roomManagerBlock.getName());
        ui.set("#RoomIcon.Background", "Hud/MiniMapAssets/"+roomManagerBlock.getShape().getId()+".png");
        return ui;
    }

    private UICommandBuilder loadCustom(Player player, String prefabPath, RoomShape shape) {
        UICommandBuilder ui = new UICommandBuilder();
        if (shape == null) return ui;

        // Save data into block
        World world = player.getWorld();
        if (world == null) return ui;

        BlockSelection selection = PrefabStore.get().getServerPrefab(prefabPath);
        String file_name = prefabPath.replace("_"+shape.name()+".prefab.json", "");
        // Generate template room placement in world as well as room data
        ui = loadTemplate(player, shape);
        world.execute(() -> {
            selection.place(player, world, new Vector3i(0, 0, 0).add(blockPos), null);
        });

        RoomManagerBlock roomManagerBlock = getBlockComponent(world);
        if (roomManagerBlock == null) return ui;
        roomManagerBlock.setShape(shape);
        String[] splitName = file_name.split("\\\\");
        if (splitName.length > 1) {
            roomManagerBlock.setType(splitName[0]);
            roomManagerBlock.setName(splitName[1]);
        } else {
            roomManagerBlock.setName(file_name);
        }

        // Set block data into page data
        ui.set("#RoomName.Value", roomManagerBlock.getName());
        ui.set("#RoomType.Value", roomManagerBlock.getType());
        ui.set("#RoomIcon.Background", "Hud/MiniMapAssets/"+roomManagerBlock.getShape().getId()+".png");
        return ui;
    }

    public static enum PageAction {
        LoadTemplate,
        LoadCustom,
        SaveCustom,
        DeleteCustom,
        SaveRoomData;

        @Nonnull
        public static final Codec CODEC = new EnumCodec(PageAction.class);

        // $FF: synthetic method
        private static PageAction[] $values() {
            return new PageAction[]{LoadTemplate, LoadCustom, SaveCustom, DeleteCustom, SaveRoomData};
        }
    }

    public static class PageEventData {
        @Nonnull
        public static final String KEY_ACTION = "Action";
        @Nonnull
        public static final String KEY_ID = "Id";
        public static final String KEY_NAME = "@Name";
        public static final String KEY_ROOM_TYPE = "@RoomType";
        @Nonnull
        public static final BuilderCodec<PageEventData> CODEC;
        public PageAction action;
        public String id;
        public String roomType;

        static {
            CODEC = BuilderCodec.builder(PageEventData.class, PageEventData::new)
                    .append(
                            new KeyedCodec<PageAction>(KEY_ACTION, PageAction.CODEC),
                            (data, value) -> data.action = value,
                            (data) -> data.action
                    )
                    .add()
                    .append(
                            new KeyedCodec<String>(KEY_ID, Codec.STRING),
                            (data, value) -> data.id = value,
                            (data) -> data.id
                    )
                    .add()
                    .append(
                            new KeyedCodec<String>(KEY_NAME, Codec.STRING),
                            (data, value) -> data.id = value,
                            (data) -> data.id
                    )
                    .add()
                    .append(
                            new KeyedCodec<String>(KEY_ROOM_TYPE, Codec.STRING),
                            (data, value) -> data.roomType = value,
                            (data) -> data.roomType
                    )
                    .add()
                    .build();
        }
    }
}
