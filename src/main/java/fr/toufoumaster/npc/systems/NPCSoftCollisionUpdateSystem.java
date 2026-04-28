package fr.toufoumaster.npc.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.npc.components.NPCSoftCollisionComponent;
import fr.toufoumaster.npc.events.NPCSoftCollisionEnterEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Vector;

public class NPCSoftCollisionUpdateSystem extends EntityTickingSystem<EntityStore> {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        if (NPCSoftCollisionComponent.getComponentType() == null) return Query.any();
        return Query.and(NPCSoftCollisionComponent.getComponentType());
    }

    @Override
    public void tick(float delta, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);

        NPCSoftCollisionComponent softCollisionComponent = commandBuffer.getComponent(ref, NPCSoftCollisionComponent.getComponentType());
        if (softCollisionComponent == null) return;

        NPCEntity npc = commandBuffer.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npc == null) return;

        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;

        BoundingBox boundingBox = commandBuffer.getComponent(ref, BoundingBox.getComponentType());
        if (boundingBox == null) return;
        Box box = boundingBox.getBoundingBox().getBox(transformComponent.getPosition());

        for (Ref<EntityStore> iref : (Vector<Ref<EntityStore>>)softCollisionComponent.getCollidingReferences().clone()) {
            if (!iref.isValid()) continue;
            TransformComponent playerTransformComponent = store.getComponent(iref, TransformComponent.getComponentType());
            if (playerTransformComponent == null) return;
            BoundingBox playerBoundingBox = store.getComponent(iref, BoundingBox.getComponentType());
            if (playerBoundingBox == null) return;
            Box pbox = playerBoundingBox.getBoundingBox().getBox(playerTransformComponent.getPosition());
            if (!box.isIntersecting(pbox)) {
                // Send OnCollisionLeave Event

                softCollisionComponent.getCollidingReferences().remove(iref);
            }
        }

        for (PlayerRef playerRef : Universe.get().getPlayers()) {
            Ref<EntityStore> pref = playerRef.getReference();
            if (pref == null || !pref.isValid()) continue;
            TransformComponent playerTransformComponent = store.getComponent(pref, TransformComponent.getComponentType());
            if (playerTransformComponent == null) return;
            BoundingBox playerBoundingBox = store.getComponent(pref, BoundingBox.getComponentType());
            if (playerBoundingBox == null) return;
            Box pbox = playerBoundingBox.getBoundingBox().getBox(playerTransformComponent.getPosition());
            if (box.isIntersecting(pbox) && !softCollisionComponent.getCollidingReferences().contains(pref)) {
                softCollisionComponent.getCollidingReferences().add(pref);
                // Send OnCollisionEnter Event
                NPCSoftCollisionEnterEvent.dispatch(ref, pref, delta);
            };
        }
    }
}