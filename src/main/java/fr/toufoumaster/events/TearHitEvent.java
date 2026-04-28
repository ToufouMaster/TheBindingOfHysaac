package fr.toufoumaster.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record TearHitEvent(@Nonnull Ref<EntityStore> tear, @Nonnull Ref<EntityStore> enemy) implements IEvent<Void> {

    public static void dispatch(@Nonnull Ref<EntityStore> tear, @Nonnull Ref<EntityStore> enemy) {
        IEventDispatcher<TearHitEvent, TearHitEvent> dispatcher = HytaleServer.get()
                .getEventBus().dispatchFor(TearHitEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new TearHitEvent(tear, enemy));
        }
    }
}

