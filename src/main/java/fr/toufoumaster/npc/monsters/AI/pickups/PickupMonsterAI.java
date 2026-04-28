package fr.toufoumaster.npc.monsters.AI.pickups;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacComponent;

import java.util.Objects;

public class PickupMonsterAI extends MonsterAI {

    @Override
    public void tick(float delta, Store<EntityStore> store, Ref<EntityStore> ref) {
        super.tick(delta, store, ref);
        if (!isAlive(store, ref)) return;
        if (ref == null || !ref.isValid()) return;

        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npcEntity == null) return;

        if (getTimeAlive() < 1.5f) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Spawn", store);
        } else {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Idle", store);
        }
    }

    @Override
    public void onPlayerContact(Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef) {
        super.onPlayerContact(store, ref, playerRef);
        if (!isAlive(store, ref)) return;
        if (ref == null || !ref.isValid()) return;
        if (getTimeAlive() < 1.5f) return;

        store.removeEntity(ref, RemoveReason.REMOVE);
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
    public boolean dealContactDamage() {
        return false;
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
