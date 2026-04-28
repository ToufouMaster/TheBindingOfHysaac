package fr.toufoumaster.npc.monsters.AI;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.npc.monsters.MonsterAI;

import java.util.Objects;

public class SpiderMonsterAI extends MonsterAI {

    private float lastDash;
    Vector3d targetPosition;

    public SpiderMonsterAI() {
        super();
        lastDash = 0f;
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
        Vector3d position = transformComponent.getPosition();
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
        if (headRotation == null) return;
        Velocity velocity = store.getComponent(ref, Velocity.getComponentType());
        if (velocity == null) return;

        final float DASH_COOLDOWN = 2.0f;
        final float DASH_DURATION = 1.0f;

        if (this.getTimeAlive() < DASH_COOLDOWN) {
            //npcEntity.playAnimation(ref, AnimationSlot.Status, "Idle", store);
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Spawn", store);
        } else if (this.getTimeAlive() > lastDash+DASH_COOLDOWN) {
            targetPosition = IsaacMonsterComponent.getNearestTarget(store,  position.clone(), 5);
            if (targetPosition == null) {
                targetPosition = position.clone().add(this.random.nextInt(-3, 3), 0, this.random.nextInt(-3, 3));
            }
            lastDash = this.getTimeAlive();
        } else if (this.getTimeAlive() < lastDash+DASH_DURATION && targetPosition != null) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Walk", store);

            Vector3d targetDirection = new Vector3d(targetPosition.getX()-position.getX(), 0, targetPosition.getZ()-position.getZ());
            //Vector3f targetRotation = new Vector3f(0, (float) Math.atan2(position.getX()-targetPosition.getX(), position.getZ()-targetPosition.getZ()), 0f);
            //headRotation.setRotation(targetRotation);
            //transformComponent.setRotation(targetRotation);

            final double MOVEMENT_SPEED = 100;
            velocity.addInstruction(targetDirection.normalize().scale(MOVEMENT_SPEED*delta), new VelocityConfig(), ChangeVelocityType.Add);
        } else {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Idle", store);
        }
    }

    @Override
    public String getModel() {
        return "SpiderModel";
    }

    @Override
    public float getModelScale() {
        return 1.5f;
    }

    @Override
    public float getMaxHealth() {
        return 6;
    }
}
