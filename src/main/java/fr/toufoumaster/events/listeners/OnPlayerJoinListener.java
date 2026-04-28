package fr.toufoumaster.events.listeners;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.IsaacGame;
import fr.toufoumaster.ui.hud.MinimapHud;

import java.awt.Color;

public class OnPlayerJoinListener {

    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        Ref<EntityStore> ref = event.getPlayerRef();
        Store<EntityStore> store = ref.getStore();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;
        IsaacGame game = IsaacGame.getInstance();

        if (game.isRunning()) {
            playerRef.getPacketHandler().disconnect(Message.translation("Game is already running."));
            return;
        }

        Universe universe = Universe.get();
        if (universe.getPlayerCount() > 1) {
            universe.getPlayers().forEach((p) -> {
                p.sendMessage(Message.raw("Multiplayer is available but has never been tested properly, it was never designed to be played like this, crashes can happen.\nHave fun anyway").color(Color.RED));
            });
        }

        World world = store.getExternalData().getWorld();

        store.addComponent(ref, Teleport.getComponentType(), new Teleport(world, new Vector3d(-3.5, 260, -52.5), new Vector3f()));

        InventoryComponent hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());
        if (hotbar == null) return;
        hotbar.getInventory().clear();
        hotbar.getInventory().addItemStack(new ItemStack("MainMenuController", 1));


        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.putComponent(TransformComponent.getComponentType(), new TransformComponent(new Vector3d(0, 250, -50), new Vector3f()));
        holder.ensureComponent(UUIDComponent.getComponentType());

        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("MainMenuModel");
        if (modelAsset == null) return;
        Model model = Model.createScaledModel(modelAsset, 1.0f);

        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        holder.addComponent(PersistentModel.getComponentType(),
                new PersistentModel(model.toReference()));
        holder.addComponent(BoundingBox.getComponentType(),
                new BoundingBox(model.getBoundingBox()));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        Ref<EntityStore> mainMenuRef = store.addEntity(holder, AddReason.SPAWN);
        if (game.getMainMenuRef() == null || !game.getMainMenuRef().isValid()) {
            game.setMainMenuRef(mainMenuRef);
        }

        ServerCameraSettings settings = new ServerCameraSettings();
        Direction direction = new Direction(
                (float) Math.toRadians(-180f),  // yaw
                (float) Math.toRadians(-90f), // pitch
                0f                            // roll
        );

        MovementStatesComponent statesComponent = store.getComponent(ref,
                MovementStatesComponent.getComponentType());
        if (statesComponent == null) return;
        MovementStates states = statesComponent.getMovementStates();
        states.flying = true;
        statesComponent.setMovementStates(states);
        settings.applyLookType = ApplyLookType.Rotation;
        settings.allowPitchControls = false;
        settings.isFirstPerson = false;
        // Notify that we provide a custom rotation in "settings.rotation"
        settings.rotationType = RotationType.Custom;
        settings.rotation = direction;
        settings.movementForceRotation = direction;
        settings.positionType = PositionType.Custom;
        settings.position = new Position(-3.5, 253, -52.5);
        settings.planeNormal = new com.hypixel.hytale.protocol.Vector3f(0, 0, 0);
        settings.displayCursor = true;
        settings.eyeOffset = true;
        settings.applyMovementType = ApplyMovementType.Position;
        settings.movementMultiplier = new com.hypixel.hytale.protocol.Vector3f(0, 0.0f, 0);
        playerRef.getPacketHandler().writeNoCache(
                new SetServerCamera(ClientCameraView.Custom, true, settings)
        );

        player.sendMessage(Message.raw("Welcome " + player.getDisplayName()));
        player.getHudManager().setCustomHud(playerRef, new MinimapHud(playerRef));
        //IsaacComponent isaacComponent = store.ensureAndGetComponent(ref, IsaacComponent.getComponentType());
        player.getHudManager().getCustomHud().update(true, new UICommandBuilder());
    }

}