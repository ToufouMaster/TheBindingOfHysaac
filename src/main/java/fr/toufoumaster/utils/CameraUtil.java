package fr.toufoumaster.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.dungeons.Room;
import fr.toufoumaster.dungeons.RoomShape;

public class CameraUtil {
    public static void setCamera(PlayerRef playerRef, RoomShape roomShape, Room room) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;
        if (room == null) return;
        Store<EntityStore> store = ref.getStore();
        store.getExternalData().getWorld().execute(() -> {
            Vector2i pos = room.getPos();
            Vector2i roomShapeSize = Room.getRoomShapeSize(roomShape);
            int distance = (roomShapeSize.getX() == 1 && roomShapeSize.getY() == 1) ? 8 : 12;
            MovementStatesComponent statesComponent = store.getComponent(ref,
                    MovementStatesComponent.getComponentType());
            if (statesComponent == null) return;
            MovementStates states = statesComponent.getMovementStates();
            states.flying = true;
            ServerCameraSettings settings = new ServerCameraSettings();
            settings.positionLerpSpeed = 0.2f;
            settings.rotationLerpSpeed = 0.2f;
            settings.isFirstPerson = false;
            settings.distance = distance;
            settings.allowPitchControls = true;
            settings.displayCursor = true;
            // Force the camera's rotation to be set by the server.
            settings.applyLookType = ApplyLookType.Rotation;
            // Notify that we provide a custom rotation in "settings.rotation"
            settings.rotationType = RotationType.Custom;
            settings.movementForceRotationType = MovementForceRotationType.Custom;
            //settings.mouseInputType = MouseInputType.LookAtPlane;
            settings.eyeOffset = true; // Not sure if necessary

            // Set the typical isometric rotation of the character movement
            Direction character_direction = new Direction(
                    (float) Math.toRadians(0f),  // yaw
                    (float) Math.toRadians(0f), // pitch
                    0f                            // roll
            );
            // Set the typical isometric rotation to the camera
            Direction direction = new Direction(
                    (float) Math.toRadians(0f),  // yaw
                    (float) Math.toRadians(-90f), // pitch
                    0f                            // roll
            );
            settings.rotation = direction;
            settings.positionType = PositionType.Custom;
            settings.position = new Position(pos.getX()*32 + 15.5, 250 + distance, pos.getY()*32 + 15.5);// new Position(-62.5f, 87f, 133.5f);
            settings.movementForceRotation = character_direction;
            settings.movementMultiplier = new Vector3f(1, 0.0f, 1);
            playerRef.getPacketHandler().writeNoCache(
                    new SetServerCamera(ClientCameraView.Custom, true, settings)
            );
        });
    }
}
