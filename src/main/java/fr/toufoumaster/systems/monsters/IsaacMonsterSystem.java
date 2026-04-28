package fr.toufoumaster.systems.monsters;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.npc.monsters.MonsterAI;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class IsaacMonsterSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float delta, int id, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(id);
        commandBuffer.getExternalData().getWorld().execute(() -> {
            if (!ref.isValid()) return;
            IsaacMonsterComponent monster = commandBuffer.getComponent(ref, IsaacMonsterComponent.getComponentType());
            if (monster == null) return;
            MonsterAI ai = monster.getAI();
            ai.tick(delta, store, ref);
        });
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(IsaacMonsterComponent.getComponentType());
    }
}
