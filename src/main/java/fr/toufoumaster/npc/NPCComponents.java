package fr.toufoumaster.npc;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.TheBindingOfHysaac;
import fr.toufoumaster.npc.components.NPCSoftCollisionComponent;
import fr.toufoumaster.npc.events.NPCSoftCollisionEnterEvent;
import fr.toufoumaster.npc.handlers.IsaacMonsterComponentCollisionEnterHandler;
import fr.toufoumaster.npc.systems.NPCSoftCollisionUpdateSystem;

public class NPCComponents {
    public static ComponentType<EntityStore, NPCSoftCollisionComponent> NPC_SOFT_COLLISION_COMPONENT_TYPE;

    public static void registerComponents(TheBindingOfHysaac plugin) {
        NPC_SOFT_COLLISION_COMPONENT_TYPE = plugin.getEntityStoreRegistry().registerComponent(NPCSoftCollisionComponent.class, "NPCSoftCollision", NPCSoftCollisionComponent.CODEC);
    }

    public static void registerSystems(TheBindingOfHysaac plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new NPCSoftCollisionUpdateSystem());
    }

    public static void registerEvents(TheBindingOfHysaac plugin) {
        plugin.getEventRegistry().register(NPCSoftCollisionEnterEvent.class, new IsaacMonsterComponentCollisionEnterHandler());
    }
}
