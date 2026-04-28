package fr.toufoumaster.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.dungeons.DungeonLayout;
import fr.toufoumaster.dungeons.DungeonLayoutGenerator;
import fr.toufoumaster.dungeons.Room;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.HashMap;
public class CameraCommands extends AbstractCommandCollection {

    public CameraCommands() {
        super("camera", "change current camera");
        addSubCommand(new IsaacCameraCommand());
        addSubCommand(new FPSCameraCommand());
        addSubCommand(new ResetCameraCommand());
    }

    public static class IsaacCameraCommand extends AbstractPlayerCommand {

        public IsaacCameraCommand() {
            super("isaac", "Default isaac camera");
        }

        @Override
        protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
            MovementStatesComponent statesComponent = store.getComponent(ref,
                    MovementStatesComponent.getComponentType());
            TransformComponent transformComponent = store.getComponent(ref,
                    TransformComponent.getComponentType());
            if (statesComponent == null) return;
            if (transformComponent == null) return;
            MovementStates states = statesComponent.getMovementStates();
            states.flying = true;
            ServerCameraSettings settings = new ServerCameraSettings();
            settings.positionLerpSpeed = 0.2f;
            settings.rotationLerpSpeed = 0.2f;
            settings.isFirstPerson = false;
            settings.distance = 6f;
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
            settings.position = new Position(transformComponent.getPosition().getX(), transformComponent.getPosition().getY()+8, transformComponent.getPosition().getZ());// new Position(-62.5f, 87f, 133.5f);
            settings.movementForceRotation = character_direction;
            settings.movementMultiplier = new Vector3f(1, 0.0f, 1);
            playerRef.getPacketHandler().writeNoCache(
                    new SetServerCamera(ClientCameraView.Custom, true, settings)
            );
        }

    }
    public static class FPSCameraCommand extends AbstractPlayerCommand {

        public FPSCameraCommand() {
            super("fps", "FPS custom camera");
        }

        @Override
        protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
            MovementStatesComponent statesComponent = store.getComponent(ref,
                    MovementStatesComponent.getComponentType());
            if (statesComponent == null) return;
            MovementStates states = statesComponent.getMovementStates();
            states.flying = true;
            ServerCameraSettings settings = new ServerCameraSettings();
            settings.positionLerpSpeed = 0.2f;
            settings.rotationLerpSpeed = 0.2f;
            settings.isFirstPerson = true;
            settings.distance = 0f;
            settings.allowPitchControls = true;
            settings.displayCursor = false;

            settings.eyeOffset = false; // Not sure if necessary
            settings.positionOffset = new Position(0, 0.5f, 0);
            settings.movementMultiplier = new Vector3f(1, 0.0f, 1);
            playerRef.getPacketHandler().writeNoCache(
                    new SetServerCamera(ClientCameraView.Custom, true, settings)
            );
        }

    }
    public static class ResetCameraCommand extends AbstractPlayerCommand {

        public ResetCameraCommand() {
            super("reset", "Reset camera to hytale's default");
        }

        @Override
        protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
            playerRef.getPacketHandler().writeNoCache(
                    new SetServerCamera(ClientCameraView.Custom, false, null)
            );
        }

    }
}
