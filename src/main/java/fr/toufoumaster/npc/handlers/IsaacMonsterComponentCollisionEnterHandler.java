package fr.toufoumaster.npc.handlers;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.npc.events.NPCSoftCollisionEnterEvent;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;

import java.util.function.Consumer;

public class IsaacMonsterComponentCollisionEnterHandler implements Consumer<NPCSoftCollisionEnterEvent> {

    @Override
    public void accept(NPCSoftCollisionEnterEvent npcSoftCollisionEnterEvent) {
        Ref<EntityStore> ref = npcSoftCollisionEnterEvent.npc();
        Ref<EntityStore> colliderRef = npcSoftCollisionEnterEvent.collider();
        if (!ref.isValid() || !colliderRef.isValid()) return;

        Store<EntityStore> store = ref.getStore();
        IsaacMonsterComponent monsterComponent = store.getComponent(ref, IsaacMonsterComponent.getComponentType());
        if (monsterComponent == null) return;
        Velocity velocity = store.getComponent(ref, Velocity.getComponentType());
        if (velocity == null) return;

        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;
        TransformComponent colliderTransformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (colliderTransformComponent == null) return;

        transformComponent.getPosition().clone().subtract(colliderTransformComponent.getPosition());

        velocity.addInstruction(transformComponent.getPosition().clone().subtract(colliderTransformComponent.getPosition()).scale(npcSoftCollisionEnterEvent.delta()*5), new VelocityConfig(), ChangeVelocityType.Add);
    }
}
