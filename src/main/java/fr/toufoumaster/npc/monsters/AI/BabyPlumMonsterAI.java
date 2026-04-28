package fr.toufoumaster.npc.monsters.AI;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacTearComponent;

import java.awt.*;
import java.util.Objects;

public class BabyPlumMonsterAI extends MonsterAI {

    private float lastDash;

    public BabyPlumMonsterAI() {
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

        final float DASH_COOLDOWN = 3.0f;
        final float DASH_DURATION = 1.0f;

        if (this.getTimeAlive() < DASH_COOLDOWN) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Spawn", store);
        } else if (this.getTimeAlive() > lastDash+DASH_COOLDOWN) {
            Vector3d targetPosition = IsaacMonsterComponent.getNearestTarget(store,  position.clone(), 19);
            if (targetPosition == null) return;
            Vector3d targetDirection = new Vector3d(targetPosition.getX()-position.getX(), 0, targetPosition.getZ()-position.getZ());

            Vector3f targetRotation = new Vector3f(0, (float) Math.atan2(position.getX()-targetPosition.getX(), position.getZ()-targetPosition.getZ()), 0f);
            position.setY(position.getY()+0.5);
            for (int i = 0; i < 10; i++) {
                IsaacTearComponent.spawnTear(ref, uuidComponent.getUuid(), position, targetRotation, 1, 10, 1.25f, 0.75f);
                targetRotation.rotateY((float) (Math.PI/5));
            }

            final double MOVEMENT_SPEED = 40;
            velocity.addInstruction(targetDirection.normalize().scale(MOVEMENT_SPEED*delta), new VelocityConfig(), ChangeVelocityType.Add);
        } else if (this.getTimeAlive() < lastDash+DASH_DURATION/2) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Attack", store);
        } else if (this.getTimeAlive() < lastDash+DASH_DURATION) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Attack2", store);
        } else {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Idle", store);
        }
    }

    @Override
    public void onDeath(Store<EntityStore> store, Ref<EntityStore> ref) {
        super.onDeath(store, ref);
        Universe.get().getPlayers().forEach(playerRef -> {
            playerRef.sendMessage(Message.raw("Thanks for playing The Binding of Hysaac.\nWe plan to release more features to add items, mobs, and a lot of QOL as well as polishing a lot more than what we had the time to.\nI'd like to thanks Hollowed Hex for the amazing models, Xoutou for the rooms, R3dline for the emotionnal support he provided, and me behind all this messy code (i promise i will clean everything).\n\nThis version of the mod is released for the Hytale New Worlds Contest ModJam.").color(Color.GREEN));
        });
    }

    @Override
    public String getModel() {
        return "BabyPlumModel";
    }

    @Override
    public float getModelScale() {
        return 2f;
    }

    @Override
    public float getMaxHealth() {
        return 750;
    }
}
