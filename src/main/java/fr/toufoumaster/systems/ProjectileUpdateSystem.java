package fr.toufoumaster.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.modules.projectile.component.Projectile;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.events.TearHitEvent;
import fr.toufoumaster.events.TearMissEvent;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacTearComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProjectileUpdateSystem extends EntityTickingSystem<EntityStore> {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        if (IsaacTearComponent.getComponentType() == null) return Query.any();
        return Query.and(IsaacTearComponent.getComponentType());
    }

    @Override
    public void tick(float delta, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        if (!ref.isValid()) return;
        Projectile projectile = commandBuffer.getComponent(ref, Projectile.getComponentType());
        if (projectile == null) return;
        TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;
        IsaacTearComponent isaacTearComponent = commandBuffer.getComponent(ref, IsaacTearComponent.getComponentType());
        if (isaacTearComponent == null) return;
        BoundingBox boundingBox = commandBuffer.getComponent(ref, BoundingBox.getComponentType());
        if (boundingBox == null) return;

        IsaacComponent isaacComponent;
        if (isaacTearComponent.getShooterUUID() != null) {
            PlayerRef playerRef = Universe.get().getPlayer(isaacTearComponent.getShooterUUID());
            if (playerRef != null) {
                Ref<EntityStore> pRef = playerRef.getReference();
                if (pRef != null && pRef.isValid())
                    isaacComponent = commandBuffer.getComponent(pRef, IsaacComponent.getComponentType());
                else {
                    isaacComponent = null;
                }
            } else {
                isaacComponent = null;
            }
        } else {
            isaacComponent = null;
        }

        Vector3d origin = isaacTearComponent.getOrigin();
        Vector3d position = transformComponent.getPosition();
        if (origin == null) return;

        double distance = position.distanceTo(origin);
        double angle = isaacTearComponent.getAngle();
        Vector3f direction = new Vector3f((float) Math.cos(angle), 0, (float) -Math.sin(angle));

        float range = isaacTearComponent.getRange()*0.75f;
        Vector3d velocity = new Vector3d(
                direction.getX(),
                (distance > range) ? -(distance-range)/2 : 0,
                direction.getZ()
        );

        if (isaacComponent != null) {
            isaacComponent.getPassiveItems().reversed().forEach((passiveItem) -> {
                passiveItem.applyTearVelocity(velocity, delta, isaacTearComponent.getAliveTime());
            });
        }

        velocity.scale(delta * (isaacTearComponent.getShootSpeed()*6.5));
        position.add(velocity);

        Box box = boundingBox.getBoundingBox().getBox(position);
        World world = commandBuffer.getExternalData().getWorld();

        world.execute(() -> {
            Selector.selectNearbyEntities(commandBuffer, position, 3, (entityStoreRef) -> {
                NPCEntity esNpc = store.getComponent(entityStoreRef, Objects.requireNonNull(NPCEntity.getComponentType()));
                PlayerRef playerRef = store.getComponent(entityStoreRef, PlayerRef.getComponentType());
                if ((isaacComponent != null && esNpc == null) || (isaacComponent == null && playerRef == null)) return;
                TransformComponent esTransformComponent = store.getComponent(entityStoreRef, TransformComponent.getComponentType());
                if (esTransformComponent == null) return;

                BoundingBox esBoundingBox = store.getComponent(entityStoreRef, BoundingBox.getComponentType());
                if (esBoundingBox == null) return;

                Box esBox = esBoundingBox.getBoundingBox().getBox(esTransformComponent.getPosition());

                boolean isIntersecting = esBox.isIntersecting(box);
                if (!isIntersecting || !ref.isValid()) return; 
                TearHitEvent.dispatch(ref, entityStoreRef);
            },  (e) -> !e.equals(ref));

            Selector.selectNearbyBlocks(position, 3, (x, y, z) -> {
                int rotation = world.getBlockRotationIndex(x, y, z);
                BlockType blockType = world.getBlockType(x, y, z);
                if (blockType == null || blockType.getId().equals("Empty")) return;


                BlockBoundingBoxes blockBoundingBoxes = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());;
                if (blockBoundingBoxes == null) return;

                Box blockBox = blockBoundingBoxes.get(rotation).getBoundingBox().getBox(x, y, z);

                boolean isIntersecting = blockBox.isIntersecting(box);

                if (!isIntersecting || !ref.isValid()) return;
                TearMissEvent.dispatch(ref);
            });

            });

        isaacTearComponent.setAliveTime(isaacTearComponent.getAliveTime()+delta);
    }
}
