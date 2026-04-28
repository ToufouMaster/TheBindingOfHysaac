package fr.toufoumaster.npc.monsters.AI;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector.Selector;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.events.IsaacHitEvent;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacComponent;

import java.util.List;
import java.util.Objects;

public class IsaacBombMonsterAI extends MonsterAI {

    float bombTimer;
    Ref<EntityStore> owner;

    @Override
    public void onCreation(Holder<EntityStore> holder) {
        super.onCreation(holder);
        bombTimer = 3f;
    }

    public void setOwner(Ref<EntityStore> ref) {
        this.owner = ref;
    }

    @Override
    public void tick(float delta, Store<EntityStore> store, Ref<EntityStore> ref) {
        super.tick(delta, store, ref);
        this.bombTimer -= delta;
        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npcEntity == null) return;

        if (getTimeAlive() < 1f) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Spawn_Lit", store);
        } else {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Lit", store);
        }
        if (bombTimer > 0f) return;
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;
        ParticleUtil.spawnParticleEffect("Explosion_Big", transformComponent.getPosition(), store);
        Selector.selectNearbyEntities(store, transformComponent.getPosition(), 2.5f, (entityStoreRef) -> {
            IsaacComponent isaacComponent = store.getComponent(entityStoreRef, IsaacComponent.getComponentType());
            IsaacMonsterComponent entityMonsterComponent = store.getComponent(entityStoreRef, IsaacMonsterComponent.getComponentType());
            if (isaacComponent != null) {
                IsaacHitEvent.dispatch(entityStoreRef, ref, 2);
            } else if (entityMonsterComponent != null) {
                MonsterAI monsterAI = entityMonsterComponent.getAI();
                if (!monsterAI.isAlive(store, entityStoreRef) || !monsterAI.canBeHit()) return;

                if (!monsterAI.isInvincible() && this.owner != null) {
                    Damage damage = new Damage(new Damage.ProjectileSource(this.owner, entityStoreRef), DamageCause.PROJECTILE, 100);
                    DamageSystems.executeDamage(entityStoreRef, store, damage);
                }
            }
        },  (e) -> !e.equals(ref));
        World world = store.getExternalData().getWorld();
        Selector.selectNearbyBlocks(transformComponent.getPosition(), 2.5f, (x, y, z) -> {
            BlockType blockType = world.getBlockType(x+1, y, z+1);
            if (blockType == null) return;
            final List<String> blockList = List.of("Rock_Basement", "Skull_Basement", "Pot_Basement");
            if (!blockList.contains(blockType.getId())) return;
            world.breakBlock(x+1, y, z+1, 0);
        });
        store.removeEntity(ref, RemoveReason.REMOVE);
    }

    @Override
    public boolean dealContactDamage() {
        return false;
    }

    @Override
    public boolean canBeHit() {
        return false;
    }

    @Override
    public boolean isInvincible() {
        return true;
    }

    @Override
    public String getModel() {
        return "Pickup_Bomb";
    }

    @Override
    public float getModelScale() {
        return 1.5f;
    }

    @Override
    public boolean isThreat() {
        return false;
    }
}
