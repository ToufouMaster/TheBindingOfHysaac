package fr.toufoumaster.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.events.IsaacHitEvent;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class NPCUpdateSystem extends EntityTickingSystem<EntityStore> {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        if (IsaacMonsterComponent.getComponentType() == null) return Query.any();
        return Query.and(IsaacMonsterComponent.getComponentType());
    }

    @Override
    public void tick(float delta, int id, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(id);
        IsaacMonsterComponent isaacMonsterComponent = commandBuffer.getComponent(ref, IsaacMonsterComponent.getComponentType());
        if (isaacMonsterComponent == null) return;

        TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;

        BoundingBox boundingBox = commandBuffer.getComponent(ref, BoundingBox.getComponentType());
        if (boundingBox == null) return;
        Box box = boundingBox.getBoundingBox().getBox(transformComponent.getPosition());

        MonsterAI monsterAI = isaacMonsterComponent.getAI();
        for (PlayerRef playerRef : Universe.get().getPlayers()) {
            Ref<EntityStore> pref = playerRef.getReference();
            if (pref == null) return;

            IsaacComponent isaacComponent = commandBuffer.getComponent(pref, IsaacComponent.getComponentType());
            if (isaacComponent == null) return;
            BoundingBox playerBoundingBox = commandBuffer.getComponent(pref, BoundingBox.getComponentType());
            if (playerBoundingBox == null) return;
            TransformComponent playerTransformComponent = commandBuffer.getComponent(pref, TransformComponent.getComponentType());
            if (playerTransformComponent == null) return;

            if (box.isIntersecting(playerBoundingBox.getBoundingBox().getBox(playerTransformComponent.getPosition()))) {
                commandBuffer.getExternalData().getWorld().execute(() -> {
                    monsterAI.onPlayerContact(store, ref, playerRef);
                });
                if (!monsterAI.dealContactDamage()) return;
                if (isaacComponent.getHealthBar().isRecovering()) continue;
                IsaacHitEvent.dispatch(pref, ref, 1);
            }
        }
    }
}
