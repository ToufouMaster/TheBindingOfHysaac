package fr.toufoumaster.npc.monsters.AI.pickups;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.utils.PickupUtils;

import java.util.Objects;
import java.util.Random;

public class PoopGoldMonsterAI extends PoopMonsterAI {

    @Override
    public void onDeath(Store<EntityStore> store, Ref<EntityStore> ref) {
        super.onDeath(store, ref);
        if (ref == null || !ref.isValid()) return;

        // Drop Items
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;

        Random random = new Random();
        for (int i = 0; i < 5 + random.nextInt(4); i++) {
            PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupCoin(store, transformComponent.getPosition(), transformComponent.getRotation()));
        }
    }

    @Override
    public float getMaxHealth() {
        return new Random().nextInt(20, 35);
    }

    @Override
    public String getModel() {
        return "PoopGoldModel";
    }
}
