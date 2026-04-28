package fr.toufoumaster.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.health.HealthBar;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PlayerUpdateSystem extends EntityTickingSystem<EntityStore> {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        if (Player.getComponentType() == null) return Query.any();
        return Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float delta, int id, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(id);
        Player player = commandBuffer.getComponent(ref, Objects.requireNonNull(Player.getComponentType()));
        if (player == null) return;
        PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;


        IsaacComponent isaacComponent = commandBuffer.getComponent(ref, IsaacComponent.getComponentType());
        if (isaacComponent == null) return;

        HealthBar healthBar = isaacComponent.getHealthBar();
        if (healthBar.isRecovering()) {
            //player.sendMessage(Message.raw("Current: "+healthBar.recoveryTimer+", add: "+delta));
            healthBar.recoveryTimer += delta;
        }
        if (!isaacComponent.canShoot()) {
            isaacComponent.tearTimer += delta;
        }

        MovementManager movementManager = commandBuffer.getComponent(ref, MovementManager.getComponentType());
        if (movementManager == null) return;
        MovementSettings ms = movementManager.getDefaultSettings();
        ms.acceleration = isaacComponent.getSpeedStat()/10f;
        movementManager.update(playerRef.getPacketHandler());

    }
}
