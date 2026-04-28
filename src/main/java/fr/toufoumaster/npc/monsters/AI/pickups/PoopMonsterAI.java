package fr.toufoumaster.npc.monsters.AI.pickups;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.utils.PickupUtils;

import java.util.Objects;
import java.util.Random;

public class PoopMonsterAI extends MonsterAI {

    @Override
    public void tick(float delta, Store<EntityStore> store, Ref<EntityStore> ref) {
        super.tick(delta, store, ref);
        if (ref == null || !ref.isValid()) return;
        if (isAlive(store, ref)) return;

        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npcEntity == null) return;
        npcEntity.playAnimation(ref, AnimationSlot.Status, "Poop_State_4", store);
    }

    @Override
    public void onDamage(Store<EntityStore> store, Ref<EntityStore> ref) {
        super.onDamage(store, ref);
        if (ref == null || !ref.isValid()) return;

        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npcEntity == null) return;
        int statIndex = EntityStatType.getAssetMap().getIndex("Health");
        EntityStatMap entityStatMapComponent = store.getComponent(ref, EntityStatsModule.get().getEntityStatMapComponentType());
        if (entityStatMapComponent == null) return;
        EntityStatValue health = entityStatMapComponent.get(statIndex);
        if (health == null) return;

        float max = health.getMax();

        if (health.get() > max - max*0.25f) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Poop_State_1", store);
        } else if (health.get() > max - max*0.5f) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Poop_State_2", store);
        } else if (health.get() > max - max*0.75f) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Poop_State_3", store);
        }
    }

    @Override
    public void onDeath(Store<EntityStore> store, Ref<EntityStore> ref) {
        super.onDeath(store, ref);
        if (ref == null || !ref.isValid()) return;
        // Drop Items
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;

        Random random = new Random();
        int drop = random.nextInt(10);
        if (drop == 0) {
            PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupCoin(store, transformComponent.getPosition(), transformComponent.getRotation()));
        } else if (drop == 1) {
            PickupUtils.applyVelocityToPickup(MonsterCommand.spawnMonster(store, "Fly", transformComponent.getPosition(), transformComponent.getRotation()));
        }
    }

    @Override
    public float getMaxHealth() {
        return new Random().nextInt(10, 25);
    }

    @Override
    public boolean dealContactDamage() {
        return false;
    }

    @Override
    public String getModel() {
        return "PoopModel";
    }

    @Override
    public float getModelScale() {
        return 2f;
    }

    @Override
    public boolean isThreat() {
        return false;
    }
}
