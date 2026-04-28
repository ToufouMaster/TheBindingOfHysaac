package fr.toufoumaster.events.listeners;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.IsaacGame;
import fr.toufoumaster.dungeons.Room;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;

import javax.annotation.Nonnull;

public class onMonsterDeathSystem extends DeathSystems.OnDeathSystem {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(IsaacMonsterComponent.getComponentType());
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        IsaacMonsterComponent monsterComponent = commandBuffer.getComponent(ref, IsaacMonsterComponent.getComponentType());
        if (monsterComponent == null) return;

        commandBuffer.getExternalData().getWorld().execute(() -> {
            monsterComponent.getAI().onDeath(store, ref);
            IsaacGame game = IsaacGame.getInstance();
            Room room = game.generatedLayout.getRoom(game.curPos);
            if (room != null) room.onMonsterDeath(store, ref);
        });
    }
}