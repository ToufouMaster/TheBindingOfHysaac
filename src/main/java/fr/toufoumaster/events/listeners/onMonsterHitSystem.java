package fr.toufoumaster.events.listeners;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class onMonsterHitSystem extends DamageEventSystem {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(IsaacMonsterComponent.getComponentType());
    }

    @Override
    public void handle(int id, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(id);
        IsaacMonsterComponent monsterComponent = commandBuffer.getComponent(ref, IsaacMonsterComponent.getComponentType());
        if (monsterComponent == null) return;

        commandBuffer.getExternalData().getWorld().execute(() -> monsterComponent.getAI().onDamage(store, ref));
    }
}