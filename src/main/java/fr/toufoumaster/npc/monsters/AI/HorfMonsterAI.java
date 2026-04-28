package fr.toufoumaster.npc.monsters.AI;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacTearComponent;

import java.util.Objects;

public class HorfMonsterAI extends MonsterAI {

    private float lastAttack;

    public HorfMonsterAI() {
        super();
        lastAttack = 0f;
    }

    @Override
    public void tick(float delta, Store<EntityStore> store, Ref<EntityStore> ref) {
        super.tick(delta, store, ref);
        if (!isAlive(store, ref)) return;
        if (ref == null || !ref.isValid()) return;
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;
        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npcEntity == null) return;
        UUIDComponent uuidComponent = store.getComponent(ref, Objects.requireNonNull(UUIDComponent.getComponentType()));
        if (uuidComponent == null) return;
        Vector3d position = transformComponent.getPosition().clone();

        final float ATTACK_COOLDOWN = 2.0f;
        if (this.getTimeAlive() < ATTACK_COOLDOWN) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Spawn", store);
        } else if (this.getTimeAlive() > lastAttack+ATTACK_COOLDOWN) {
            Vector3d targetPosition = IsaacMonsterComponent.getNearestTarget(store, position, 7);
            if (targetPosition == null) return;
            Vector3f targetRotation = new Vector3f(0, (float) Math.atan2(position.getX()-targetPosition.getX(), position.getZ()-targetPosition.getZ()), 0f);
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Attack", store);
            position.setY(position.getY()+0.5);
            IsaacTearComponent.spawnTear(ref, uuidComponent.getUuid(), position, targetRotation, 1, 8, 1.5f, 1f);
            setLastAttack(this.getTimeAlive());
        } else if (this.getTimeAlive() > lastAttack+0.67) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Shake", store);
        }
    }

    private void setLastAttack(float time) {
        this.lastAttack = time;
    }

    @Override
    public String getModel() {
        return "HorfModel";
    }

    @Override
    public float getModelScale() {
        return 1.5f;
    }
}
