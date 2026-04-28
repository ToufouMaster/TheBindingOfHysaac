package fr.toufoumaster.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.IsaacGame;
import fr.toufoumaster.dungeons.Room;
import fr.toufoumaster.dungeons.RoomShape;
import fr.toufoumaster.utils.CameraUtil;
import fr.toufoumaster.utils.RoomRegister;

import javax.annotation.Nonnull;

import static fr.toufoumaster.dungeons.Room.getRoomShapeSizeScaled;

public class IsaacDoorEnterInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<IsaacDoorEnterInteraction> CODEC = BuilderCodec.builder(
            IsaacDoorEnterInteraction.class, IsaacDoorEnterInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    long lastInteraction = 0;

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        long curTime = System.currentTimeMillis();
        if (curTime < lastInteraction+1000) return;
        lastInteraction = curTime;
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) return;
        World world = commandBuffer.getExternalData().getWorld();
        IsaacGame game = IsaacGame.getInstance();
        world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            Ref<EntityStore> pRef = interactionContext.getEntity();
            if (store.getComponent(pRef, PlayerRef.getComponentType()) == null) return;
            Room room = game.generatedLayout.getRoom(game.curPos);
            BlockPosition blockPosition = interactionContext.getTargetBlock();
            Vector2i size = getRoomShapeSizeScaled(room.getShape());
            Vector2i roomPosition = new Vector2i(room.getPos().getX()*32 + ((size.getX()-1)/2), room.getPos().getY()*32+(size.getY()/2));
            assert blockPosition != null;
            int doorId = RoomRegister.getDoorIdFromCoords(room.getShape(), new Vector2i(blockPosition.x, blockPosition.z).subtract(roomPosition));
            if (doorId == -1) return;
            Vector2i newRoomCoords = room.getNeighborCoords(doorId);
            Room newRoom = game.generatedLayout.getRoom(newRoomCoords);
            if (newRoom == null) return;
            room.onRoomLeave(store);

            Vector2i newRoomPos = newRoom.getPos();
            Vector2i roomEntranceOffset = newRoom.getPos().clone().subtract(newRoomCoords);
            game.curPos = newRoomCoords;
            newRoom.onRoomEntered(store);
            RoomShape newRoomShape = newRoom.getShape();
            Vector2i newRoomSize = getRoomShapeSizeScaled(newRoomShape);
            int newDoorId = Room.getOppositeDoorIndex(doorId, roomEntranceOffset);
            Vector2i newRoomDoorPos = RoomRegister.shapeDoorLocations.get(newRoomShape)[newDoorId];
            Vector3d teleportPosition = new Vector3d(newRoomPos.x * 32 + newRoomDoorPos.x + (double) (newRoomSize.x) / 2, 252, newRoomPos.y * 32 + newRoomDoorPos.y + (double) (newRoomSize.y) / 2);
            for (PlayerRef playerRef : Universe.get().getPlayers()) {
                CameraUtil.setCamera(playerRef, newRoomShape, newRoom);
                Ref<EntityStore> ref = playerRef.getReference();
                if (ref == null || !ref.isValid()) continue;
                store.addComponent(ref, Teleport.getComponentType(), new Teleport(world, teleportPosition, new Vector3f()));

                Player player = store.getComponent(pRef, Player.getComponentType());
                if (player == null) return;
                CustomUIHud hud = player.getHudManager().getCustomHud();
                if (hud == null) return;
                hud.update(true, new UICommandBuilder());
            }
            //world.setBlock((int) teleportPosition.x, (int) (teleportPosition.y+3), (int) teleportPosition.z, "Cloth_Block_Wool_Black");
        });
    }
}