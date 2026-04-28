package fr.toufoumaster.npc.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record NPCSoftCollisionEnterEvent(@Nonnull Ref<EntityStore> npc, @Nonnull Ref<EntityStore> collider, double delta) implements IEvent<Void> {

    public static void dispatch(@Nonnull Ref<EntityStore> npc, @Nonnull Ref<EntityStore> collider, double delta) {
        IEventDispatcher<NPCSoftCollisionEnterEvent, NPCSoftCollisionEnterEvent> dispatcher = HytaleServer.get()
                .getEventBus().dispatchFor(NPCSoftCollisionEnterEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new NPCSoftCollisionEnterEvent(npc, collider, delta));
        }
    }
}
