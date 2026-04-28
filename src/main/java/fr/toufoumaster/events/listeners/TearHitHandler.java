package fr.toufoumaster.events.listeners;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.events.IsaacHitEvent;
import fr.toufoumaster.events.TearHitEvent;
import fr.toufoumaster.events.TearMissEvent;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacTearComponent;

import java.util.Objects;
import java.util.UUID;
import java.util.Vector;
import java.util.function.Consumer;

public class TearHitHandler implements Consumer<TearHitEvent> {

    @Override
    public void accept(TearHitEvent event) {
        Ref<EntityStore> ref = event.tear();
        Ref<EntityStore> victimRef = event.enemy();
        if (!ref.isValid() || !victimRef.isValid()) return;

        Store<EntityStore> store = ref.getStore();

        IsaacTearComponent isaacTearComponent = store.getComponent(ref, IsaacTearComponent.getComponentType());
        if (isaacTearComponent == null) return;

        PlayerRef playerRef = Universe.get().getPlayer(isaacTearComponent.getShooterUUID());
        if (playerRef != null) { // If shooter is a Player
            Ref<EntityStore> pRef = playerRef.getReference();
            if (pRef == null || !pRef.isValid()) return;
            IsaacComponent isaacComponent = store.getComponent(pRef, IsaacComponent.getComponentType());
            if (isaacComponent == null) return;
            IsaacMonsterComponent monsterComponent = store.getComponent(victimRef, IsaacMonsterComponent.getComponentType());
            if (monsterComponent == null) return;
            MonsterAI monsterAI = monsterComponent.getAI();
            if (!monsterAI.isAlive(store, victimRef) || !monsterAI.canBeHit()) return;

            Vector<String> usedItemName = new Vector<>();
            isaacComponent.getPassiveItems().reversed().forEach((passiveItem) -> {
                if (usedItemName.contains(passiveItem.getName())) return;
                passiveItem.onTearHit(ref, victimRef);
                usedItemName.add(passiveItem.getName());
            });

            if (!monsterAI.isInvincible()) {
                Damage damage = new Damage(new Damage.ProjectileSource(pRef, ref), DamageCause.PROJECTILE, isaacTearComponent.getDamage());
                DamageSystems.executeDamage(victimRef, store, damage);
            }
        } else { // If shooter is an ennemy
            playerRef = store.getComponent(victimRef, PlayerRef.getComponentType());
            if (playerRef == null) return;
            UUID worldUUID = playerRef.getWorldUuid();
            if (worldUUID == null) return;
            World world = Universe.get().getWorld(worldUUID);
            if (world == null) return;
            Ref<EntityStore> shooterRef = world.getEntityStore().getRefFromUUID(isaacTearComponent.getShooterUUID());
            if (shooterRef == null) return;
            IsaacHitEvent.dispatch(victimRef, shooterRef, (int)isaacTearComponent.getDamage());
        }

        store.removeEntity(ref, RemoveReason.REMOVE);
    }
}